$(document).ready(function(){
    $.ajax('/api/logs',
        {
            method: 'GET',
            success: function(data, status){
                console.log('successfully completed request!');
                console.log(data);
                setLogTable(data.logs);
            }
        }
    );

});

function setLogTable(logs){
    var tBody = document.createElement('tbody');

    for(var i = 0; i < logs.length; ++i)
        tBody.appendChild(generateTableRow(logs[i]));

    var tHeadRow = document.createElement('tr');

    tHeadRow.appendChild(document.createElement('th'));
    tHeadRow.appendChild(document.createElement('th'));
    tHeadRow.appendChild(document.createElement('th'));
    tHeadRow.appendChild(document.createElement('th'));

    var tHead = document.createElement('thead');
    tHead.appendChild(tHeadRow);

    var table = document.createElement('table');
    table.className = 'table';
    table.appendChild(tHead);
    table.appendChild(tBody);


    var responsive_table = document.createElement("div");
    responsive_table.className = 'table-responsive';
    responsive_table.appendChild(table);

    $("#logs_container").append(responsive_table);
}

function generateTableRow(log_data){

    var idCol = document.createElement('td');
    var idText = document.createTextNode(log_data.id)
    idCol.appendChild(idText);

    var dateCol = document.createElement('td');
    var dateText = document.createTextNode(log_data.date);
    dateCol.appendChild(dateText);

    var userCol = document.createElement('td');
    var userText = document.createTextNode(log_data.user);
    userCol.appendChild(userText);

    var button = generateButtonToShowInfo(log_data);

    var moreInfoCol = document.createElement('td');
    moreInfoCol.appendChild(button);

    var row = document.createElement('tr');

    var exhaustionLevel = extractExhaustionLevelFromLog(log_data);
    console.log('level: '+exhaustionLevel);

    if(exhaustionLevel) {
        console.log('getting color');
        console.log(getContextualColor(exhaustionLevel));
        row.className = getContextualColor(exhaustionLevel);
    }

    row.appendChild(idCol);
    row.appendChild(dateCol);
    row.appendChild(userCol);
    row.appendChild(moreInfoCol);

    return row;
}

function generateButtonToShowInfo(log_data){
    var a = document.createElement('button');
    a.type = 'button';
    a.className = 'btn btn-success';
    var t = document.createTextNode('more info');
    a.appendChild(t);

    a.onclick = showExtraInfoCallback(log_data);

    return a;
}

function showExtraInfoCallback(log_data){
    return function(){
        // Clear the current text.
        $('#info_container').html('');

        var title_elem_type = 'h6';

        // Prepare and show the new element/s.
        var logNumbTitle = document.createElement(title_elem_type);
        var t = document.createTextNode('Log # '+log_data.id);
        logNumbTitle.appendChild(t);
        $('#info_container').append(logNumbTitle);

        var logUserTitle = document.createElement(title_elem_type);
        var t2 = document.createTextNode(log_data.user);
        logUserTitle.appendChild(t2);
        $('#info_container').append(logUserTitle);

        var context_table = document.createElement('table-condensed');

        // Table Head
        var tHeadRow = document.createElement('tr');
        tHeadRow.appendChild(document.createElement('th'));
        tHeadRow.appendChild(document.createElement('th'));
        tHeadRow.appendChild(document.createElement('th'));

        var tHead = document.createElement('thead');
        tHead.appendChild(tHeadRow);
        context_table.appendChild(tHead);

        // Table Body
        var tBody = document.createElement('tbody');

        // Table Rows
        for(var i = 0; i < log_data.contexts.length; ++i){
            var row = document.createElement('tr');

            // Icon Column
            var iconCol = document.createElement('td');
            var span = document.createElement('span');
            span.className = 'glyphicon ';
            span.className += getIconForContextType(log_data.contexts[i].type);
            iconCol.style.paddingRight = '10px';
            iconCol.appendChild(span);
            row.appendChild(iconCol);

            // Type Column
            var typeCol = document.createElement('td');
            var typeText = document.createTextNode(log_data.contexts[i].type);
            typeCol.appendChild(typeText);
            typeCol.style.paddingRight = '20px';
            row.appendChild(typeCol);

            //Value Column
            var valCol = document.createElement('td');
            var valText = document.createTextNode(log_data.contexts[i].value);
            valCol.appendChild(valText);
            valCol.style.textAlign = 'right';
            row.appendChild(valCol);

            tBody.appendChild(row);
        }

        context_table.appendChild(tBody);

        $('#info_container').append(context_table);
    }
}

function extractExhaustionLevelFromLog(log){
    var exhaustionElem = log.contexts.filter(function(elem){return elem.type == 'EXHAUSTION'});

    console.log(exhaustionElem[0]);

    return exhaustionElem.length == 0 ? undefined : exhaustionElem[0].value;
}

const CONTEXTUAL_EXHAUSTION_COLORS = ['', 'bg-success', 'bg-warning', 'bg-danger'];

function getContextualColor(exhaustionLevel){
    return CONTEXTUAL_EXHAUSTION_COLORS[exhaustionLevel];
}

const CONTEXT_ICON_NAMES = {
    'EXHAUSTION': 'glyphicon-bed',
    'TIME': 'glyphicon-time',
    'LOCATION': 'glyphicon-globe',
    'WEATHER': 'glyphicon-tree-conifer'
}

function getIconForContextType(type){
    return CONTEXT_ICON_NAMES[type];
}