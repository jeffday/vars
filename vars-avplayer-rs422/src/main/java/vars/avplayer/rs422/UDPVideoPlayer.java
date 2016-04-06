package vars.avplayer.rs422;

import org.bushe.swing.event.EventBus;
import org.mbari.util.Tuple2;
import org.mbari.vcr4j.rs422.RS422VideoIO;
import org.mbari.vcr4j.udp.UDPError;
import org.mbari.vcr4j.udp.UDPState;
import org.mbari.vcr4j.udp.UDPVideoIO;
import vars.ToolBelt;
import vars.annotation.VideoArchive;
import vars.annotation.VideoArchiveDAO;
import vars.avplayer.ImageCaptureService;
import vars.avplayer.VideoController;
import vars.avplayer.VideoPlayer;
import vars.avplayer.VideoPlayerDialogUI;
import vars.avplayer.rx.SetVideoArchiveMsg;
import vars.avplayer.rx.SetVideoControllerMsg;
import vars.shared.rx.RXEventBus;
import vars.shared.ui.GlobalStateLookup;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Brian Schlining
 * @since 2016-04-06T09:58:00
 */
public class UDPVideoPlayer implements VideoPlayer<UDPState, UDPError> {

    private final AtomicReference<UDPVideoIO> videoIORef = new AtomicReference<>();
    private ImageCaptureService imageCaptureService = ImageCaptureServiceRef.getImageCaptureService();
    private UDPVideoPlayerDialogUI dialogUI;

    @Override
    public boolean canPlay(String mimeType) {
        return false;
    }

    /**
     *
     * @param toolBelt
     * @param args The arguments need to connect to you video control service.
     *             [hostName: String,
     *             port: Integer
     *             platformName: String,
     *             sequenceNumber: Integer,
     *             tapeNumber: Integer,
     *             isHD: Boolean]
     * @return
     */
    @Override
    public Optional<Tuple2<VideoArchive, VideoController<UDPState, UDPError>>> openVideoArchive(ToolBelt toolBelt, Object... args) {
        String hostName = (String) args[0];
        Integer port = (Integer) args[1];
        String platformName = (String) args[2];
        Integer sequenceNumber = (Integer) args[3];
        Integer tapeNumber = (Integer) args[4];
        Boolean isHD = (Boolean) args[5];
        UDPVideoParams videoParams = new UDPVideoParams(hostName, port, platformName, sequenceNumber, tapeNumber, isHD);
        return openVideoArchive(toolBelt, videoParams);
    }

    public Optional<Tuple2<VideoArchive, VideoController<UDPState, UDPError>>> openVideoArchive(ToolBelt toolBelt, UDPVideoParams videoParams) {
        VideoArchiveDAO dao = toolBelt.getAnnotationDAOFactory().newVideoArchiveDAO();
        VideoArchive videoArchive = dao.findOrCreateByParameters(videoParams.getPlatformName(),
                videoParams.getSequenceNumber(),
                videoParams.getVideoArchiveName());
        dao.close();

        setUDPConnection(videoParams.getHostName(), videoParams.getPort());
        return Optional.of(new Tuple2<>(videoArchive, getVideoController()));
    }

    private void setUDPConnection(String hostname, Integer port) {
        if (videoIORef.get() != null) {
            UDPVideoIO io = videoIORef.get();
            io.close();
            videoIORef.set(null);
        }

        try {
            UDPVideoIO io = new UDPVideoIO(hostname, port);
            videoIORef.set(io);
        }
        catch (Exception e) {
            EventBus.publish(GlobalStateLookup.TOPIC_NONFATAL_ERROR, e);
        }
    }

    private VideoController<UDPState, UDPError> getVideoController() {
        return new VideoController<>(imageCaptureService, videoIORef.get());
    }

    @Override
    public VideoPlayerDialogUI<UDPState, UDPError> getConnectionDialog(ToolBelt toolBelt, RXEventBus eventBus) {
        if (dialogUI == null) {
            Window window = GlobalStateLookup.getSelectedFrame();
            dialogUI = new UDPVideoPlayerDialogUI(window, toolBelt);
            dialogUI.onOkay(() -> {
                VideoArchive videoArchive = dialogUI.openVideoArchive();
                Tuple2<String, Integer> params = dialogUI.getRemoteConnectionParams();
                setUDPConnection(params.getA(), params.getB());
                eventBus.send(new SetVideoArchiveMsg(videoArchive));
                eventBus.send(new SetVideoControllerMsg<>(getVideoController()));
            });
        }
        return dialogUI;
    }

    @Override
    public String getName() {
        return "Remote VCR Connection";
    }


}
