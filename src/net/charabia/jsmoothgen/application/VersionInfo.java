/**
 * File Name: VersionInfo.java
 * 
 * Copyright (c) 2015 BISON Schweiz AG, All Rights Reserved.
 */

package net.charabia.jsmoothgen.application;

/**
 * @author christian.oetterli
 */
public class VersionInfo {
  private String binaryFileVersion;
  private String binaryProductVersion;
  private String productVersion;
  private String companyName;
  private String fileDescription;
  private String legalCopyright;
  private String originalFilename;
  private String productName;

  public String getProductVersion() {
    return productVersion;
  }

  public void setProductVersion(String productVersion) {
    this.productVersion = productVersion;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getFileDescription() {
    return fileDescription;
  }

  public void setFileDescription(String fileDescription) {
    this.fileDescription = fileDescription;
  }

  public String getLegalCopyright() {
    return legalCopyright;
  }

  public void setLegalCopyright(String legalCopyright) {
    this.legalCopyright = legalCopyright;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public void setOriginalFilename(String originalFilename) {
    this.originalFilename = originalFilename;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getBinaryFileVersion() {
    return binaryFileVersion;
  }

  public void setBinaryFileVersion(String binaryFileVersion) {
    this.binaryFileVersion = binaryFileVersion;
  }

  public String getBinaryProductVersion() {
    return binaryProductVersion;
  }

  public void setBinaryProductVersion(String binaryProductVersion) {
    this.binaryProductVersion = binaryProductVersion;
  }
}
