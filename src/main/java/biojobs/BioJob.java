package biojobs;

import converters.LocalDateTimeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name="biojobs")
public class BioJob {

    @Id
    @SequenceGenerator(name="pk_sequence_biojobs", sequenceName="biojobs_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pk_sequence_biojobs")
    @Column(name="BIOJOB_ID")
    private int biojobId;

    @NotNull
    @Column(name="JOB_ID")
    private Integer jobId;

    @Column(name="COOKIE_ID")
    private String cookieId;

    @NotNull
    @Column(name="FINISHED")
    private boolean finished;

    @NotNull
    @Column(name="PROGRAM_NAME")
    private String programName;

    @Column(name="EMAIL")
    private String email;

    @NotNull
    @Convert(converter = LocalDateTimeConverter.class)
    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "JOB_DATE", columnDefinition="TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime jobDate;

    @OneToMany(mappedBy="biojob", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @org.hibernate.annotations.Cascade( {org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<BioJobResult> bioJobResultList = new LinkedList<>();

    public int getBiojobId() {
        return biojobId;
    }

    public void setBiojobId(int biojobId) {
        this.biojobId = biojobId;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getProgramNameName() {
        return programName;
    }

    public void setProgramNameName(String programName) {
        this.programName = programName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getJobDate() {
        return jobDate;
    }

    public void setJobDate(LocalDateTime jobDate) {
        this.jobDate = jobDate;
    }

    public List<BioJobResult> getBioJobResultList() {
        return bioJobResultList;
    }

    public void addToBioJobResultList(BioJobResult bioJobResult) {
        this.bioJobResultList.add(bioJobResult);
    }

    public Integer isJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getCookieId() {
        return cookieId;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }
}
