(function(){var c=this;c.config={};var b=function(){return new a()};function a(){this.init()}a.prototype={init:function(){window.onerror=function(h,e,p,i,n){var d,l=window;function o(s){var r,f;if(typeof(s)=="object"){if(s===null){return"null"}if(window.JSON&&window.JSON.stringify){return JSON.stringify(s)}f=j(s);r=[];for(var q in s){r.push((f?"":('"'+q+'":'))+o(s[q]))}r=r.join();return f?("["+r+"]"):("{"+r+"}")}else{if(typeof(s)=="undefined"){return"undefined"}else{if(typeof(s)=="number"||typeof(s)=="function"){return s.toString()}}}return !s?'""':('"'+s+'"')}function j(f){return"[object Array]"==Object.prototype.toString.call(f)}i=i||(l.event&&l.event.errorCharacter)||0;if(!!n&&!!n.stack){h=n.stack.toString()}else{if(!!arguments.callee){var g=[h];var k=arguments.callee.caller,m=3;while(k&&(--m>0)){g.push(k.toString());if(k===k.caller){break}k=k.caller}g=g.join(",");h=g}}d=o(h)+(e?";URL:"+e:"")+(p?";Line:"+p:"")+(i?";Column:"+i:"");if(l._last_err_msg){if(l._last_err_msg.indexOf(h)>-1){return}l._last_err_msg+="|"+h}else{l._last_err_msg=h}setTimeout(function(){console.log("ERROR:"+d);var r=encodeURIComponent(d),q=new Image;if(c.config.url){q.src="//wq.jd.com/webmonitor/collect/badjs.json?Content="+r+"&t="+Math.random()+"&refer="+encodeURIComponent(config.url)}else{q.src="//wq.jd.com/webmonitor/collect/badjs.json?Content="+r+"&t="+Math.random()}},1000);return false}},badjsConfig_callback:function(d){c.config=d?d:{}}};return MonitorBadJsObject=b()})();