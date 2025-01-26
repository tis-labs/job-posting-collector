package posting.job.collector.service;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

@Component
public class UrlDecoder {
    public String decodeUrl(String encodeUrl) throws UnsupportedEncodingException {
        String decodedUrl = encodeUrl.replace("\\u003d", "=")
                .replace("\\u0026", "&");
        return URLDecoder.decode(decodedUrl,"UTF-8");
    }

}
