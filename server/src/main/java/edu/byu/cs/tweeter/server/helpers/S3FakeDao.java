package edu.byu.cs.tweeter.server.helpers;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;

public class S3FakeDao {

    private static final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_WEST_2)
            .build();
    private static final String S3_BUCKET = "miniaturehobbit";
    private static final String S3_IMAGE_URL = "";
    private static final String DEFAULT_IMAGE = "";

    public String createPicture(RegisterRequest request) {
        byte[] data = Base64.decode(request.getImage()); // Must be base 64 encoded!
        InputStream stream = new ByteArrayInputStream(data);

        File file = null;
        String name = request.getUsername();
        try {
            file = new File(String.valueOf(Files.createTempFile(name, ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[data.length + 1];
            while ((read = stream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //String imageURL = file.getName();

        s3.putObject(new PutObjectRequest(S3_BUCKET, file.getName(), file).withCannedAcl(CannedAccessControlList.PublicRead));

        String imageURL = "https://miniaturehobbit.s3.us-west-2.amazonaws.com/" + file.getName();

        return imageURL;
        /*return "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";*/
    }


}
