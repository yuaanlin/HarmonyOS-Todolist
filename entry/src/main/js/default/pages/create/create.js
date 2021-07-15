import router from '@system.router';

export default {
    data: {
        id: '',
        title: '',
        description: '',
        deadline: '2012-5-6',
        done: false
    },
    goBack() {
        router.back();
    },
    handleDeadlineChange(e) {
        this.deadline = e.year + '-' + (e.month + 1) + '-' + e.day
    },
    handleTitleChange(e) {
        this.title = e.value;
    },
    handleDescriptionChange(e) {
        this.description = e.value
    },
    submit: async function() {
        await this.$app.$def.updateOrCreateTodo({
            id: this.$app.$def.data.todoList[this.$app.$def.data.todoList.length - 1].id + 1,
            title: this.title,
            description: this.description,
            deadline: this.deadline,
            done: this.done
        })
        router.back();
    }
}
