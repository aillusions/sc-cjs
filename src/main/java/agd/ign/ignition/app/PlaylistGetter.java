package agd.ign.ignition.app;

import agd.ign.ignition.dto.put.NewSongDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @author aillusions
 */
public class PlaylistGetter {

    private final String STORAGE_PATH;

    public PlaylistGetter(String path) {
        STORAGE_PATH = path;
    }

    //private static final String STORAGE_PATH = "down";
    //public static final String STORAGE_PATH = "g:\\env\\media";

    private static final String PLAYLIST_FILE_NAME = "playlist.m3u8";
    private static final String META_FILE_NAME = "metadata.json";

    private static final String PL_FILE_NAME_PREFIX = "https://cf-hls-media.sndcdn.com/playlist/";

    private static final String PL_FILE_NAME_POSTFIX = "/playlist.m3u8?Policy";

    /**
     * https://cf-hls-opus-media.sndcdn.com/playlist/X8CS2k3gj9mG.64.opus/playlist.m3u8?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLWhscy1vcHVzLW1lZGlhLnNuZGNkbi5jb20vcGxheWxpc3QvWDhDUzJrM2dqOW1HLjY0Lm9wdXMvcGxheWxpc3QubTN1OCIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTUxMTM1NDU0Nn19fV19&Signature=o~0kzT98UbC2JAe9ZPipqcTT2yXCR4FWJUwXwGdWzglEEXKtnN0kioud3fmuU9onNfzAKWaOajVAkIHtQEFHZzZ4thlcK-3bII1NsS1dWAMumpon0Ru8VG0PwJZ~NWBR0Z1yzasI1lPeJESF1jFf~irN9uw0ZX3WUnGjbCh4mqeGXhFjkoKdkIZdAI14GDHGNqTJbuS6kUxmTqKKh1TQwW2W8wkf7INHvki65uwy1ARiOuAytKWx~StrgNwPEJS-KwOKRt1e7xyfkThnnCiY5oL94CODqIKYTvGiQ-vdumkeXjvwpd2CSusBSDIqr2Tg~YWqM-wGTlLLqJfH6EptCA__&Key-Pair-Id=APKAJAGZ7VMH2PFPW6UQ
     * https://cf-hls-media.sndcdn.com/playlist/7BSlpZTiK3pe.128.mp3/playlist.m3u8?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLWhscy1tZWRpYS5zbmRjZG4uY29tL3BsYXlsaXN0LzdCU2xwWlRpSzNwZS4xMjgubXAzL3BsYXlsaXN0Lm0zdTgiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE1MTExMDQ1MTN9fX1dfQ__&Signature=PgbJpqIyCK2G08qV4aWH7PBl3fpnnh6RjL6h4FE6LbOFxbNQANJisYhr5KnLiWRx20CoyonMiTGrNDCtE~sCASixafs~MoqeEhM60rfHNOCKw86NR0hyXBwqZUr5eVolxZny8SYgebnR-~QMQ8uxWtDkG-2LprDW8EwZwHKevOIqHHel~8oEiEBukmTSYGqH6cZLbmXztTJ82wej7bz6m5K1ntMReMJHfnoMSZbas5K3u1vx32e3x3fgN~xN3a4GPzeWlO76w4dLvQxc5vAa65c2Uour1Nbnu3y93~oyWjm-1R~KOCl53A9Ykt-N8BBx3RXwSl4YVA~Q2FKRaDHsGw__&Key-Pair-Id=APKAJAGZ7VMH2PFPW6UQ
     */
    public void downloadPlayList(String url, Path playlistFilePath) throws IOException {

        String playListFilePathStr = playlistFilePath.toAbsolutePath().toString();

        System.out.println("Saving playlist: " + playListFilePathStr);

        try (InputStream in = SoundcloudAccessor.getInputStream(url)) {
            Files.copy(in, playlistFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static String getSongId(String url) {
        String mp3Id = StringUtils.substringBetween(url, PL_FILE_NAME_PREFIX, PL_FILE_NAME_POSTFIX);
        if (StringUtils.isNotBlank(mp3Id)) {
            return mp3Id;
        }

        throw new RuntimeException("Unexpected url: " + url);
    }

    public Path getAbsoluteStoragePath() {
        return new File(STORAGE_PATH).toPath();
    }

    public Path getSongBasePath(String songId) {
        return new File(STORAGE_PATH + File.separator + songId).toPath().toAbsolutePath();
    }

    public Path getSongFragmentPath(String songId, String fileName) {
        return new File(STORAGE_PATH + File.separator + songId + File.separator + fileName).toPath().toAbsolutePath();
    }

    public String getSongBasePathStr(String songId) {
        String rv = getSongBasePath(songId).toString();
        new File(rv).mkdir();
        return rv;
    }

    public Path getSongPlaylistPath(String songId) {
        return new File(getSongBasePathStr(songId) + File.separator + PLAYLIST_FILE_NAME).toPath();
    }

    public Path getSongMetadataPath(String songId) {
        return new File(getSongBasePathStr(songId) + File.separator + META_FILE_NAME).toPath();
    }

    public static String getMp3FileName(int i) {
        return "" + i + ".mp3";
    }

    public Path getSongMp3Path(int i, String songId) {
        return new File(getSongBasePathStr(songId) + File.separator + getMp3FileName(i)).toPath();
    }

    /**
     * https://cf-hls-media.sndcdn.com/media/0/31762/EniKZcU3hSCh.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLWhscy1tZWRpYS5zbmRjZG4uY29tL21lZGlhLyovKi9FbmlLWmNVM2hTQ2guMTI4Lm1wMyIsIkNvbmRpdGlvbiI6eyJEYXRlTGVzc1RoYW4iOnsiQVdTOkVwb2NoVGltZSI6MTUxMTEwNjYwM319fV19&Signature=i7q~eQjY2IOi7dhC-GZR8cx-Em2o~kQBs7afM28UaxKGbHTGPcn4RWy5tjEgjfh6FbLoJ9m6s20jeTSZUnOQohKwGt8IDcAt7E15UQyjlxjht8rD9CuTbRjetAbYHUfv1JQRxZSimcbHU48QJh2dWa-EB1TFQPWAEDPCv580PXFokfrK-O4GSUctKMn4EjgqPKrzyKhWktoIG6zFnJv9rhWZc8oHLQ3iNOVC33UX8h8vkvxdAoFtC0SA~vNbSKx-orbNgMNrlyMuHQw2Jk3P4nJhSezG0h3JNQ6JakGN1QkTlC91q~z-exsr14TiJ2YlfmukWT8a4ENAVFgX6Bellw__&Key-Pair-Id=APKAJAGZ7VMH2PFPW6UQ
     */
    public static void downloadFragment(Path mp3Path, String url) throws IOException {
        try (InputStream in = SoundcloudAccessor.getInputStream(url)) {
            Files.copy(in, mp3Path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void saveMetadata(Path metaPath, NewSongDto dto) throws FileNotFoundException, JsonProcessingException {
        PrintWriter out = new PrintWriter(metaPath.toFile());
        out.println(new ObjectMapper().writeValueAsString(dto));
        out.close();
    }


    public void downloadFragments(NewSongDto dto) {

        String url = dto.getScCjsSongPlayListUrl();
        try {

            String songId = PlaylistGetter.getSongId(url);

            Path playlistFilePath = getSongPlaylistPath(songId);
            if (playlistFilePath.toFile().exists()) {
                throw new RuntimeException("Already indexed: " + songId);
            }
            downloadPlayList(url, playlistFilePath);

            List<String> partUrls = PlaylistReader.getPartUrls(playlistFilePath);

            int i = 0;
            for (String partUrl : partUrls) {

                Path fragmentPath = getSongMp3Path(i, songId);

                String playListFilePathStr = fragmentPath.toAbsolutePath().toString();
                System.out.println("Saving fragment: " + playListFilePathStr + " (" + (i + 1) + " of " + partUrls.size() + ")");

                PlaylistGetter.downloadFragment(fragmentPath, partUrl);

                i++;
            }

            Path metaPath = getSongMetadataPath(songId);
            PlaylistGetter.saveMetadata(metaPath, dto);

            System.out.println("Done: " + dto.getScCjsSongTitle());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}



