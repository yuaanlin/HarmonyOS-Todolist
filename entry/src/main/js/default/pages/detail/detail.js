import router from '@system.router';

export default {
    data: {
        todo: {
            id: 0,
            title: '',
            description: '',
            deadline: '',
            done: false
        }
    },
    onShow() {
        this.todo = this.$app.$def.data.todoList.find(t => t.id === this.todo.id)
    },
    goBack() {
        router.back()
    },
    goEdit() {
        router.push({
            uri: 'pages/edit/edit',
            params: {
                todo: this.todo
            }
        })
    }
}
