package norman.junk.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
public class DataFile {
    @Id
    @GeneratedValue
    private Long id;
    @Version
    private Integer version = 0;
    private String originalFilename;
    private String contentType;
    private Long size;
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadTimestamp;
    @Enumerated(EnumType.STRING)
    private DataFileStatus status;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dataFile")
    private List<DataLine> dataLines = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(Date uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public DataFileStatus getStatus() {
        return status;
    }

    public void setStatus(DataFileStatus status) {
        this.status = status;
    }

    public List<DataLine> getDataLines() {
        return dataLines;
    }

    public void setDataLines(List<DataLine> dataLines) {
        this.dataLines = dataLines;
    }
}