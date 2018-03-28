package biojobs;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="biojob_results")
public class BioJobResult {

    @Id
    @SequenceGenerator(name="pk_sequence_biojobs_res", sequenceName="biojobs_res_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pk_sequence_biojobs_res")
    @Column(name="BIOJOB_RES_ID")
    private int BIOJOB_RES_ID;

    @ManyToOne
    @JoinColumn(name="BIOJOB_ID", nullable = false)
    private BioJob biojob;

    @NotNull
    @Column(name="RESULT_FILE_NAME")
    private String resultFileName;

    @NotNull
    @Column(name="RESULT_FILE")
    private String resultFile;

    public BioJob getBiojob() {
        return biojob;
    }

    public void setBiojob(BioJob biojob) {
        this.biojob = biojob;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public int getBIOJOB_RES_ID() {
        return BIOJOB_RES_ID;
    }

    public void setBIOJOB_RES_ID(int BIOJOB_RES_ID) {
        this.BIOJOB_RES_ID = BIOJOB_RES_ID;
    }
}
