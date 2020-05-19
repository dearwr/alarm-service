//提示信息
var showTips = function (head, msg, time) {
    tips_head.text(head);
    tips_msg.text(msg);
    tips.show();
    tips.fadeOut(time);
}