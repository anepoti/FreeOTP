function createCookie(name,value,days,path) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path="+path;
}
function eraseCookie(name,path) {
    //alert('createCookie('+name+',,-1,'+path+')');
    createCookie(name,'',-1,path);
}
function eraseCookies(list,path) {
    var li = list.split(';');
    //alert('cookies.length='+cookies.length);
    for(var j=0;j < li.length;j++) {
            var cprefix = li[j];
            //alert('cprefix='+cprefix);
            for(var i=0;i < cookies.length;i++) {
                    var c = cookies[i];
                    //alert('cookie='+c);
                    while (c.charAt(0)==' ') c = c.substring(1,c.length);
                    if (c.indexOf(cprefix) == 0)    {
                            var cname = c.substring(0,c.indexOf('='));
                            //alert('cookie name='+cname);
                            createCookie(cname,"",-1, path);
                    }
            }
    }
    return null;
}
// esempio per rimuovere un cookie di un altro path ... createCookie('LFR_SESSION_STATE_9001',"",-1,'/web/10265/');