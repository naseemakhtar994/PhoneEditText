package com.github.leoxnidas.phoneedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.support.annotation.RequiresApi;


public class PhoneEditText extends EditText {

    private final int p_mDefaultCode = Codes.USA;

    private int p_mCode;
    private String p_mCodeStr;
    private String PLUS = "+";

    public PhoneEditText(Context context) {
        super(context);
        initialize();
        setCode(p_mDefaultCode);
    }

    public PhoneEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPhoneEditText(attrs);
        initialize();
    }

    public PhoneEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
        setCode(p_mDefaultCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhoneEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
        setCode(p_mDefaultCode);
    }

    private void initialize() {
        // no matter what kind of input type developer will use
        // always, phone input type shall be used.
        setInputType(-1);
        addTextChangedListener(null);
        setSingleLine(true);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
    }

    private void initPhoneEditText (AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.PhoneEditText, 0, 0);

        try {
            p_mCode = typedArray.getInt(R.styleable.PhoneEditText_code, p_mDefaultCode);
        } finally {
            typedArray.recycle();
        }

        setCode(p_mCode);
    }

    public void setCode(int code) {
        if(Codes.isCodeValid(code)) {
            p_mCode = code;
            p_mCodeStr = Codes.asString(code);
            setText((PLUS + p_mCodeStr));
            Selection.setSelection(getText(), getText().toString().length());
        } else {
            throw new RuntimeException("International code does not exists.");
        }
    }

    public int getCode() {
        return p_mCode;
    }

    @Override
    public void setInputType(int type) {
        super.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(new PhoneEditTextWatcher());
    }

    private class PhoneEditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // if user intent to write a number
            // at international code space,
            // that number shall be added next
            // to the international code
            // of the string, automatically
            //
            // Example:
            //
            // user intents to write this
            //
            //  ---------------
            // | +518         |
            //  --------------
            //
            // and the result shall be this
            //
            //  ---------------
            // | +581         |
            //  --------------
            //
            String currentText = s.toString();
            int currentLength = currentText.length();

            String code = PLUS + p_mCodeStr;
            int codeLength = code.length();

            if(currentLength < codeLength)
                return;

            for(int i = 0; i < codeLength; i++) {
                if(currentText.charAt(i) != code.charAt(i)) {
                    String nCode = code;

                    for(int j = codeLength + 1; j < currentLength; j++) {
                        nCode += Character.toString(currentText.charAt(j));
                    }

                    nCode += Character.toString(currentText.charAt(i));
                    setText(nCode);
                    break;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() < (1 + p_mCodeStr.length())) {
                setText((PLUS + p_mCodeStr));
                Selection.setSelection(getText(), (PLUS + p_mCodeStr).length());
            }

            if(getSelectionStart() <= p_mCodeStr.length() - 1)
                Selection.setSelection(s, s.length());
        }
    }

}
