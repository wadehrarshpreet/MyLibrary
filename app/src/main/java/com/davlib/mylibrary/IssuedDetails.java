package com.davlib.mylibrary;

import java.io.Serializable;

public class IssuedDetails  implements Serializable {
    private String mRollNo;
    private String mDateOfReturn;

    public String getmRollNo() {
        return mRollNo;
    }

    public void setmRollNo(String mRollNo) {
        this.mRollNo = mRollNo;
    }

    public String getmDateOfReturn() {
        return mDateOfReturn;
    }

    public void setmDateOfReturn(String mDateOfReturn) {
        this.mDateOfReturn = mDateOfReturn;
    }
}
