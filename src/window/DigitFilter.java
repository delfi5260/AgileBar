package window;

import javax.swing.text.*;

public class DigitFilter extends DocumentFilter {
    private String DIGITS;
    public DigitFilter(int type){
        switch (type){
            case 0: this.DIGITS="\\d+"; break;
            default: this.DIGITS="(.*)[А-я A-z](.*)";
        }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string.matches(DIGITS)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
        if (string.matches(DIGITS) || string.isEmpty()) {
            super.replace(fb, offset, length, string, attrs);
        }
    }
}