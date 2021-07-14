package com.example.baunews.HelperClasses;

import android.content.res.Resources;

import com.example.baunews.R;
import com.google.android.material.textfield.TextInputLayout;

public class Validation {

    Resources res;

    public Validation(Resources res) {
        this.res = res;
    }

    public Boolean validateEmail(TextInputLayout email) {
        String val = email.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            email.setError(res.getString(R.string.field_cannot_be_empty));
            email.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            email.setError(res.getString(R.string.invalid_email_address));
            email.requestFocus();
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    public Boolean validatePassword(TextInputLayout password) {
        String val = password.getEditText().getText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        if (val.isEmpty()) {
            password.setError(res.getString(R.string.field_cannot_be_empty));
            password.requestFocus();
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError(res.getString(R.string.password_is_too_weak));
            password.requestFocus();
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public Boolean validateConfirmPassword(TextInputLayout cp, String p) {
        String cpTxt = cp.getEditText().getText().toString();
        if (!cpTxt.equals(p)) {
            cp.setError(res.getString(R.string.password_matching));
            cp.requestFocus();
            return false;
        }
        cp.setError(null);
        cp.setErrorEnabled(false);
        return true;
    }

    public Boolean validateCollage(TextInputLayout collage, String selected) {
        if (selected.equals("")) {
            collage.setError(res.getString(R.string.select_collage_error));
            collage.requestFocus();
            return false;
        }
        collage.setError(null);
        collage.setErrorEnabled(false);
        return true;
    }

    public boolean validateLoginEmail(TextInputLayout email) {
        String Email = email.getEditText().getText().toString().trim();

        if (Email.isEmpty()) {
            email.setError(res.getString(R.string.field_cannot_be_empty));
            email.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError(res.getString(R.string.invalid_email_address));
            email.requestFocus();
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    public boolean validateLoginPassword(TextInputLayout password) {
        String Password = password.getEditText().getText().toString().trim();

        if (Password.isEmpty()) {
            password.setError(res.getString(R.string.field_cannot_be_empty));
            password.requestFocus();
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
}
