import prompt from '@system.prompt'

export default {
    data: {
        todoList: []
    },
    onCreate() {
        console.info('AceApplication onCreate');
    },
    onDestroy() {
        console.info('AceApplication onDestroy');
    },
    updateOrCreateTodo: async function(todo) {
        var action = {
            bundleName: 'com.linyuanlin.todolist',
            abilityName: '.DataBridgeAbility',
            messageCode: 1002,
            data: todo,
            abilityType: 0
        };
        var result = await FeatureAbility.callAbility(action);
        var todos = JSON.parse(result)
        this.data.todoList = todos;
    },
    deleteTodo: async function(todo) {
        var action = {
            bundleName: 'com.linyuanlin.todolist',
            abilityName: '.DataBridgeAbility',
            messageCode: 1003,
            data: todo,
            abilityType: 0
        };
        var result = await FeatureAbility.callAbility(action);
        var todos = JSON.parse(result)
        this.data.todoList = todos;
    },
    refresh: async function() {
        var action = {
            bundleName: 'com.linyuanlin.todolist',
            abilityName: '.DataBridgeAbility',
            messageCode: 1001,
            abilityType: 0
        };
        var result = await FeatureAbility.callAbility(action);
        var todos = JSON.parse(result)
        this.data.todoList = todos;
    },
};
