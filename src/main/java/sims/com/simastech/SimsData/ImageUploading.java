package sims.com.simastech.SimsData;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "ImageProfile")
public class ImageUploading {

    @Id
    private String id;
    private String filename;

    private String filetype;
    @Lob
    @Column(columnDefinition = "BYTEA") // Untuk PostgreSQL
    private byte[] fileData;

    public ImageUploading() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public ImageUploading(String id, String filename, String filetype, byte[] fileData) {
        this.id = id;
        this.filename = filename;
        this.filetype = filetype;
        this.fileData = fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

}
