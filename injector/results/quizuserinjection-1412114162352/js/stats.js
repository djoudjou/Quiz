var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "100000",
        "ok": "99990",
        "ko": "10"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "0",
        "ko": "10"
    },
    "maxResponseTime": {
        "total": "14403",
        "ok": "14403",
        "ko": "30"
    },
    "meanResponseTime": {
        "total": "1",
        "ok": "1",
        "ko": "12"
    },
    "standardDeviation": {
        "total": "63",
        "ok": "63",
        "ko": "5"
    },
    "percentiles1": {
        "total": "3",
        "ok": "3",
        "ko": "21"
    },
    "percentiles2": {
        "total": "6",
        "ok": "6",
        "ko": "28"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 99986,
        "percentage": 100
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 4,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 10,
        "percentage": 0
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "414,8",
        "ok": "414,8",
        "ko": "0,04"
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
        "total": "100000",
        "ok": "99990",
        "ko": "10"
    },
    "minResponseTime": {
        "total": "0",
        "ok": "0",
        "ko": "10"
    },
    "maxResponseTime": {
        "total": "14403",
        "ok": "14403",
        "ko": "30"
    },
    "meanResponseTime": {
        "total": "1",
        "ok": "1",
        "ko": "12"
    },
    "standardDeviation": {
        "total": "63",
        "ok": "63",
        "ko": "5"
    },
    "percentiles1": {
        "total": "3",
        "ok": "3",
        "ko": "21"
    },
    "percentiles2": {
        "total": "6",
        "ok": "6",
        "ko": "28"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 99986,
        "percentage": 100
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 4,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 10,
        "percentage": 0
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "414,8",
        "ok": "414,8",
        "ko": "0,04"
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
