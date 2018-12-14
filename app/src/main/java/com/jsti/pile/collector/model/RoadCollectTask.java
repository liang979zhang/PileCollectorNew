package com.jsti.pile.collector.model;

import java.io.Serializable;

/**
 * 道路收藏任务
 */
public class RoadCollectTask implements Serializable {
    private static final long serialVersionUID = -4925422780068148933L;
    //
    public static final int COLLECT_STATUS_NEVER_START = 1;
    public static final int COLLECT_STATUS_COLLECTING = 2;
    public static final int COLLECT_STATUS_PAUSED = 3;
    public static final int COLLECT_STATUS_FINISHED = 4;
    //
    private String roadId;
    private String roadName;
    private String roadCode;
    private String direction;
    private String directionName;
    private int startPile;
    private int lastFindPile;
    private double lastPileLongitude;
    private double lastPileLatitude;
    private double lastScanLongitude;
    private double lastScanLatitude;
    private long startTime;
    private long finishTime;
    private String creatorId;
    private String creatorName;
    private boolean isCollectPileASC;// 顺着桩号收集
    private int collectStatus = COLLECT_STATUS_NEVER_START;
    private boolean isSubmitToServer = false;
    private int gpsReferCount;
    private int exactPileCount;
    private String filePath;
    private boolean isStartPileCollected = false;

    public String getRoadId() {
        return roadId;
    }

    public void setRoadId(String roadId) {
        this.roadId = roadId;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getRoadCode() {
        return roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }

    public int getStartPile() {
        return startPile;
    }

    public void setStartPile(int startPile) {
        this.startPile = startPile;
    }

    public int getLastFindPile() {
        return lastFindPile;
    }

    public void setLastFindPile(int lastFindPile) {
        this.lastFindPile = lastFindPile;
    }

    public double getLastScanLongitude() {
        return lastScanLongitude;
    }

    public void setLastScanLongitude(double lastScanLongitude) {
        this.lastScanLongitude = lastScanLongitude;
    }

    public double getLastScanLatitude() {
        return lastScanLatitude;
    }

    public void setLastScanLatitude(double lastScanLatitude) {
        this.lastScanLatitude = lastScanLatitude;
    }

    public double getLastPileLongitude() {
        return lastPileLongitude;
    }

    public void setLastPileLongitude(double lastPileLongitude) {
        this.lastPileLongitude = lastPileLongitude;
    }

    public double getLastPileLatitude() {
        return lastPileLatitude;
    }

    public void setLastPileLatitude(double lastPileLatitude) {
        this.lastPileLatitude = lastPileLatitude;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public boolean isCollectPileASC() {
        return isCollectPileASC;
    }

    public void setCollectPileASC(boolean isCollectPileASC) {
        this.isCollectPileASC = isCollectPileASC;
    }

    public int getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(int collectStatus) {
        this.collectStatus = collectStatus;
    }

    public boolean isSubmitToServer() {
        return isSubmitToServer;
    }

    public void setSubmitToServer(boolean isSubmitToServer) {
        this.isSubmitToServer = isSubmitToServer;
    }

    public int getGpsReferCount() {
        return gpsReferCount;
    }

    public void setGpsReferCount(int gpsReferCount) {
        this.gpsReferCount = gpsReferCount;
    }

    public int getExactPileCount() {
        return exactPileCount;
    }

    public void setExactPileCount(int exactPileCount) {
        this.exactPileCount = exactPileCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isStartPileCollected() {
        return isStartPileCollected;
    }

    public void setStartPileCollected(boolean isStartPileCollected) {
        this.isStartPileCollected = isStartPileCollected;
    }

    public String makePostToServerFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCreatorId());
        sb.append("_");
        sb.append(getRoadId());
        sb.append("_");
        sb.append(getDirection());
        sb.append("_");
        sb.append(getStartPile());
        sb.append("_");
        sb.append(getStartTime());
        sb.append("_");
        sb.append(getFinishTime());
        sb.append(".piles");
        return sb.toString();
    }

    /**
     * 文件名字创建
     *
     * @return
     */
    public String makeLocalSaveFileName() {


        StringBuilder sb = new StringBuilder();
        sb.append(getCreatorId());
        sb.append("_");
        sb.append(getRoadId());
        sb.append("_");
        sb.append(getDirection());
        sb.append("_");
        sb.append(getStartPile());
        sb.append("_");
        sb.append(System.currentTimeMillis());
        sb.append(".piles");
        return sb.toString();
    }
}
