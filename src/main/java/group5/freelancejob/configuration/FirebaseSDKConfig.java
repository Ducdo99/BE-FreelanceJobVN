package group5.freelancejob.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseSDKConfig {
//    private final ResourceLoader _resourceLoader;
//    private Resource _resource;
//    private InputStream _inputStream;

//    public FirebaseSDKConfig(ResourceLoader resourceLoader) throws IOException {
//        _resourceLoader = resourceLoader;
//        _resource = _resourceLoader.getResource("classpath:firebase-config.json");
//        _inputStream = _resource.getInputStream();
//    }

//    @Bean
//    public FirebaseApp createFirebase() throws IOException {
//        FirebaseOptions opts = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(_inputStream)).build();
//        return FirebaseApp.initializeApp(opts);
//    }
}
