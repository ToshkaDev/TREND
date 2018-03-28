package enums;

/**
 * Created by vadim on 7/27/17.
 */
public enum Delimeters {
    BAR("|"), TAB("$'\t'"), COMMA(","), SEMICOLON(":");

    private String delim;

    Delimeters(String delim) {
        this.delim = delim;
    }

    public String getDelim() {
        return delim;
    }


}
