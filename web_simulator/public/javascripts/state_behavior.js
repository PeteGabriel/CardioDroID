'use strict'
window.onload = function() {

    /** Set event handlers and other setup options */
    setAwaitARPcontent();
    setStateButtonHandlers();

    /** State Behavior */

    var states = {
        "level_1": "/states/set?level=LOW",
        "level_2": "/states/set?level=MEDIUM",
        "level_3": "/states/set?level=HIGH"
    };

    function sendTiredSignal(levelQuery) {
        var xHttp = new XMLHttpRequest();
        xHttp.onreadystatechange = function() {
            if (xHttp.readyState == 4 && xHttp.status == 200) {
                console.log("Exhaustion State sent successfully!");
            }
        };
        xHttp.open("GET", levelQuery, true);
        xHttp.send(null);
    }

    function setStateButtonHandlers() {
        document.getElementById('TiredLevel1').onclick = function() {
            sendTiredSignal(states.level_1);
        };
        document.getElementById('TiredLevel2').onclick = function() {
            sendTiredSignal(states.level_2);
        };
        document.getElementById('TiredLevel3').onclick = function() {
            sendTiredSignal(states.level_3);
        };
    }

    /** Authentication/Identification client side behaviour */

    const ARP_QUERY_REFRESH_TIME = 2000;
    const AIM_QUERY_REFRESH_TIME = 2000;


    // Function queries the server on the ARP state, if no state is defined,
    // then we schedule another query to be made in 3 seconds.
    function getARProtocol(){
        var xHttp = new XMLHttpRequest();

        xHttp.onload = arpResponseHandler;
        xHttp.open("GET", "/protocol", true);
        xHttp.send();
    }

    function arpResponseHandler(){
        // valid value was available.
        if(this.status == 200){
            let resp = JSON.parse(this.response);

            console.log("ARP query successful: "+resp.state);

            setIdentificationOrAuthenticateContent(resp.state);
        }
        // No valid value was available.
        else if(this.status == 404){
            console.log("Scheduling another ARP query to be made in: "+ARP_QUERY_REFRESH_TIME+" ms")
            scheduleCall(ARP_QUERY_REFRESH_TIME, getARProtocol);
            return false;
        }
    }

    // Function queries the server on the AIM state, the state we are interested in is OK / NOT_OK
    // Queries until there is an available value.
    function getAIMprotocol(){
        var xHttp = new XMLHttpRequest();

        xHttp.onload = aimResponseHandler;
        xHttp.open("GET", "/patterns/auth-status", true);
        xHttp.send();
    }

    function aimResponseHandler(){
        // Value was vailable
        if(this.status == 200) {
            var aim_value = JSON.parse(this.responseText).state;
            console.log("AIM query successful: " + aim_value);

            if (aim_value == "OK")
                showExhaustionStateTab("Authentication was successful");
            else if (aim_value == "NOT_OK")
                console.log("AUTHENTICATION WAS \'NOT_OK\'");// TODO
        // No valid value was available.
        }else if(this.status == 404){
                console.log("Scheduling another AIM query to be made in: "+AIM_QUERY_REFRESH_TIME+" ms")
                scheduleCall(AIM_QUERY_REFRESH_TIME, getAIMprotocol);
                return false;
        }
    }

    function setPatternDropdownContent(){
        var xHttp = new XMLHttpRequest();

        xHttp.onload = patternResponseHandler;
        xHttp.open("GET", "/patterns", true);
        xHttp.send();
    }

    function patternResponseHandler(){
        if(this.status == 200){
            setHtmlContentbyId('#pattern-dropdown',
                generatePatternOptionsForDropdown( JSON.parse(this.response).patterns ));
        }
    }

    function setSendPatternOnClickHandler(){
        $('#send-pattern').click(sendPattern);
    }

    // Handle sending the selected pattern.
    function sendPattern() {
        console.log("Send Button Pressed!");
        let sel_pattern = $('#pattern-dropdown option:selected').text();

        let xHttp = new XMLHttpRequest();
        xHttp.onload = patternPostHandler;
        xHttp.open("POST", "/patterns", true);
        xHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xHttp.send("pattern="+sel_pattern);
    }

    function patternPostHandler() {
        if(this.status == 204){
            console.log("Pattern was Received successfully!")
        }
        else if(this.status == 404){
            var respObj = JSON.parse(this.response);
            console.log("Pattern supplied error: "+respObj.error)
        }
        else if(this.status == 500){
            var respObj = JSON.parse(this.response);
            console.log("Internal Server error occurred: "+respObj.error);
        }

        // Check for an authentication result.
        scheduleCall(AIM_QUERY_REFRESH_TIME, getAIMprotocol);
    }

    /** HTML content creators/setters **/

    function setAwaitARPcontent() {
        const view_uri = "/partials/awaitARP-partial.html"

        var xHttp = new XMLHttpRequest();
        xHttp.onload = function(){
            setHtmlContentbyId('#pattern-process', this.responseText);

            // Possibly Place a button at the end of the section, to force the check of the state.
            scheduleCall(500, getARProtocol);
        };
        xHttp.open("GET", view_uri);
        xHttp.send();
    }

    function setIdentificationOrAuthenticateContent(process) {
        // obtain the partial view
        const view_uri = "/partials/auth-partial.html"

        var xHttp = new XMLHttpRequest();
        xHttp.onload = function(){
            setHtmlContentbyId("#pattern-process", this.responseText);
            setHtmlContentbyId("#selected-arp", process + " Mode Selected");
            setPatternDropdownContent();
            setSendPatternOnClickHandler();
        };
        xHttp.open("GET", view_uri);
        xHttp.send();
    }

    function showExhaustionStateTab(msg){
        console.log("Switching Tabs!");
        $("#tab2").prop('checked', true);
        setHtmlContentbyId("#auth-state-message", msg)
    }

    function generatePatternOptionsForDropdown(patterns) {
        let htmlStr = "";
        for(var i = 0; i< patterns.length; ++i){
            htmlStr +="<option>"+patterns[i]+"</option>";
        }
        return htmlStr;
    }

    function setHtmlContentbyId(html_elem_id, content){
        $(html_elem_id).html(content);
    }

    /** Miscellaneous Functions */

    function scheduleCall(time, callback){
        setTimeout(callback, time);
    }


    function selectOption(idx){
      switch(idx){
        case 1:
            return states.level_1;
          break;
        case 2:
            return states.level_2;
          break;
        case 3:
            return states.level_3;
          break;
      }
    }

    //button to simulate states
    var handler;
    $('#SimulatorButton').click(function(evt){
      var content = $(this).text();
      if (content === 'Simulate'){
        handler = setInterval(function(){
          var idx = Math.floor((Math.random() * 3) + 1);
          sendTiredSignal(selectOption(idx));
        }, 2000);
        $(this).text("Stop Simulation");
      }else {
        clearTimeout(handler);
        $(this).text("Simulate");
      }
    });




};
