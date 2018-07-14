$.post('userAction_getCurrentUser', function(result) {
    var currentUser = result.data[0];
},'json');

$.ajaxSetup({
    cache : false,
    contentType : "application/x-www-form-urlencoded;charset=utf-8",
    complete : function (XHR, textStatus) {
        var resText = XHR.responseText;
        if (resText == 'ajaxSessionTimeOut') {
            sessionTimeOut();
        }
    }
});

function sessionTimeOut() {
    var url = window.document.location.href;
    var pathName = window.document.location.pathname;
    var projectName = pathName.substring(1, pathName.substring(1).indexOf("/") + 1);
    var host = url.substring(7, url.substring(7).indexOf("/") + 7);
    layer.alert('当前会话已过时', {icon : 5, title : '系统提示'});
    setTimeout(function (){
        window.top.location.href = 'http://' + host + '/' + projectName + '/page/login.html';
    }, '2000');
}
