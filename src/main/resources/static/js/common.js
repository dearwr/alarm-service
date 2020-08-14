//提示信息
var showTips = function (head, msg, time) {
    tips_head.text(head);
    tips_msg.text(msg);
    tips.show();
    tips.fadeOut(time);
}

var alarm_service_url = "http://47.112.150.247:7676/";