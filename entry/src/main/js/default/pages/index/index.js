import router from '@system.router';

export default {
    data: {
        todoList: [],
        selectedIndex: -1
    },
    onInit() {
        this.rerender();
    },
    onClick(todo) {
        router.push({
            uri: "pages/detail/detail",
            params: {
                todo
            }
        });
    },
    goToCreate() {
        router.push({
            uri: "pages/create/create"
        })
    },
    onWantDeleted(idx) {
        this.selectedIndex = idx;
        this.$element("apiMenu").show({
            x: 360,
            y: 100 + idx * 36
        });
    },
    onShow() {
        this.refresh();
    },
    onMenuSelected: async function(e) {
        switch (e.value) {
            case 'edit':
                router.push({
                    uri: 'pages/edit/edit',
                    params: {
                        todo: this.todoList[this.selectedIndex]
                    }
                })
                break;
            case 'delete':
                await this.$app.$def.deleteTodo(this.todoList[this.selectedIndex])
                this.refresh();
                break;
        }
    },
    rerender() {
        this.todoList = this.$app.$def.data.todoList.sort((a, b) => a.id - b.id);
    },
    doneTodo(todo) {
        todo.done = !todo.done;
        this.$app.$def.updateTodo(todo)
        this.rerender();
    },
    refresh: async function() {
        await this.$app.$def.refresh();
        this.rerender();
    }
}
