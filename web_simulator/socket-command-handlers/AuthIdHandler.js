module.exports = function(aim_container){
    return function(state){
        aim_container.setValue(state);
    }
}
