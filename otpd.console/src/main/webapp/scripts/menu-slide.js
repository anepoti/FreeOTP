 $(function(){
    if (self.location.href == top.location.href){
      
       
       
      var logo=$("<a href='menu-slide.html'><img id='logo' border='0' src='images/OTPDLogo.png' alt='otpd' style='display:none;'></a>");
      $("body").prepend(logo);
      $("#logo").fadeIn();
      
       
    }
    
    
    
    $(".vertMenu").buildMenu(
    	    {
    	      template:"menuVoices.html",
    	      menuWidth:170,
    	      openOnRight:true,
    	      menuSelector: ".menuContainer",
    	      iconPath:"ico/",
    	      hasImages:true,
    	      fadeInTime:200,
    	      fadeOutTime:200,
    	      adjustLeft:0,
    	      adjustTop:0,
    	      opacity:.95,
    	      openOnClick:false,
    	      minZindex:200,
    	      shadow:false,
    	      hoverIntent:300,
    	      submenuHoverIntent:300,
    	      closeOnMouseOut:true
    	    });

    	    $(document).buildContextualMenu(
    	    {
    	      template:"menuVoices.html",
    	      menuWidth:200,
    	      overflow:2,
    	      menuSelector: ".menuContainer",
    	      iconPath:"ico/",
    	      hasImages:false,
    	      fadeInTime:100,
    	      fadeOutTime:100,
    	      adjustLeft:0,
    	      adjustTop:0,
    	      opacity:.99,
    	      closeOnMouseOut:false,
    	      onContextualMenu:function(o,e){}, //params: o,e
    	      shadow:false
    	    });



    	    $("#extruderLeft").buildMbExtruder({
    	      position:"left",
    	      width:300,
    	      extruderOpacity:.8,
    	      onExtOpen:function(){},
    	      onExtContentLoad:function(){},
    	      onExtClose:function(){}
    	    });



  });
 
 