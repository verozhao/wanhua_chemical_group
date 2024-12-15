import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.*;
import java.util.Properties;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class ETLPipeline {

    // AWS S3 credentials
    private static final String ACCESS_KEY = "your_access_key";
    private static final String SECRET_KEY = "your_secret_key";
    private static final String REGION = "your_region";
    private static final String BUCKET_NAME = "your_bucket_name";

    public static void main(String[] args) {
        try {
            // Step 1: Extract data from Oracle database
            String oracleUrl = "jdbc:oracle:thin:@your_host:1521:your_service_name";
            String username = "your_username";
            String password = "your_password";

            Connection connection = DriverManager.getConnection(oracleUrl, username, password);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM YOUR_TABLE";
            ResultSet resultSet = statement.executeQuery(query);

            // Step 2: Transform data (convert to CSV format)
            String csvFilePath = "output.csv";
            FileWriter fileWriter = new FileWriter(csvFilePath);
            fileWriter.append("Column1,Column2,Column3\n");

            while (resultSet.next()) {
                String col1 = resultSet.getString("COLUMN1");
                String col2 = resultSet.getString("COLUMN2");
                String col3 = resultSet.getString("COLUMN3");
                fileWriter.append(String.format("%s,%s,%s\n", col1, col2, col3));
            }

            fileWriter.flush();
            fileWriter.close();
            System.out.println("Data extracted and transformed successfully!");

            // Step 3: Load data to AWS S3
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(REGION)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();

            File csvFile = new File(csvFilePath);
            s3Client.putObject(BUCKET_NAME, "path/to/output.csv", csvFile);
            System.out.println("Data uploaded to S3 successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
