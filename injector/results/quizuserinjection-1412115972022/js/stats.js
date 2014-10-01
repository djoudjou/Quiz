var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "10",
        "ok": "10",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "12",
        "ok": "12",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "5541",
        "ok": "5541",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "566",
        "ok": "566",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "1658",
        "ok": "1658",
        "ko": "-"
    },
    "percentiles1": {
        "total": "3057",
        "ok": "3057",
        "ko": "-"
    },
    "percentiles2": {
        "total": "5044",
        "ok": "5044",
        "ko": "-"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 9,
        "percentage": 90
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 1,
        "percentage": 10
    },
    "group4": {
        "name": "failed",
        "count": 0,
        "percentage": 0
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "1,70",
        "ok": "1,70",
        "ko": "-"
    }
},
contents: {
"create-user-2ef59": {
        type: "REQUEST",
        name: "Create_User",
path: "Create_User",
pathFormatted: "create-user-2ef59",
stats: {
    "name": "Create_User",
    "numberOfRequests": {
        "total": "10",
        "ok": "10",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "12",
        "ok": "12",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "5541",
        "ok": "5541",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "566",
        "ok": "566",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "1658",
        "ok": "1658",
        "ko": "-"
    },
    "percentiles1": {
        "total": "3057",
        "ok": "3057",
        "ko": "-"
    },
    "percentiles2": {
        "total": "5044",
        "ok": "5044",
        "ko": "-"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 9,
        "percentage": 90
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 1,
        "percentage": 10
    },
    "group4": {
        "name": "failed",
        "count": 0,
        "percentage": 0
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "1,70",
        "ok": "1,70",
        "ko": "-"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
