package my.vaadin.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.ui.Upload.Receiver;

public class UploadReceiver implements Receiver {
	OutputStream outputFile = null;

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		File file=null;
        try {
            file = new File("C:/pobrane/"+filename);
            if(!file.exists()) {
                file.createNewFile();
            }
            outputFile =  new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
	}
}
