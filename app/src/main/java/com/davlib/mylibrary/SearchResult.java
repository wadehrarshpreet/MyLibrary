package com.davlib.mylibrary;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Arshpreet on 9/24/2015.
 */
public class SearchResult implements Serializable {
    private String mQuantity;
    private String mTitle;
    private String mDescription;
    private String mAuthor;
    private String mISBN;
    private String mFullDescription;
    private String mPublisher;
    private String mPublishedDate;
    private String mImage;
    private ArrayList<IssuedDetails> mIssuedDetails;

    public ArrayList<IssuedDetails> getmIssuedDetails() {
        return mIssuedDetails;
    }

    public void setmIssuedDetails(ArrayList<IssuedDetails> mIssuedDetails) {
        this.mIssuedDetails = mIssuedDetails;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }

    public String getmPublisher() {
        return mPublisher;
    }

    public void setmPublisher(String mPublisher) {
        this.mPublisher = mPublisher;
    }

    public String getmPublishedDate() {
        return mPublishedDate;
    }

    public void setmPublishedDate(String mPublishedDate) {
        this.mPublishedDate = mPublishedDate;
    }

    public String getmFullDescription() {
        return mFullDescription;
    }

    public void setmFullDescription(String mFullDescription) {
        this.mFullDescription = mFullDescription;
    }

    public String getmISBN() {
        return mISBN;
    }

    public void setmISBN(String mISBN) {
        this.mISBN = mISBN;
    }

    public String getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }
}