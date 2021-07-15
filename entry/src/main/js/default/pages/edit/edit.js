import router from '@system.router';

export default {
    data: {
        todo: {
            id: 0,
            title: '',
            description: '',
            deadline: '2012-5-6',
            done: false
        },
        title: '',
        description: '',
        deadline: '2012-5-6',
        isContinue: false
    },
    goBack() {
        if (this.isContinue) {
            router.push({
                uri: 'pages/index/index'
            })
            return
        }
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
    onShow() {
        this.title = this.todo.title;
        this.description = this.todo.description
        this.deadline = this.todo.deadline
    },
    submit: async function () {
        await this.$app.$def.updateOrCreateTodo({
            id: this.todo.id,
            title: this.title,
            description: this.description,
            deadline: this.deadline,
            done: this.todo.done
        })
        if (this.isContinue) {
            router.push({
                uri: 'pages/index/index'
            })
            return
        }
        router.back();
    },
    tryContinueAbility: async function() {
        await FeatureAbility.continueAbility();
        if (this.isContinue) {
            router.push({
                uri: 'pages/index/index'
            })
            return
        }
        router.back();
    },
    onRestoreData(restoreData) {
        this.todo = {
            id: restoreData.todo.id,
            title: restoreData.title,
            description: restoreData.description,
            deadline: restoreData.deadline,
            done: restoreData.todo.done
        };
        this.isContinue = true;
    },
    onStartContinuation() {
        return true;
    },
    onCompleteContinuation(code) {
        console.info("nCompleteContinuation: code = " + code);
    },
    onSaveData(saveData) {
        var data = {
            todo: this.todo,
            title: this.title,
            description: this.description,
            deadline: this.deadline
        };
        Object.assign(saveData, data)
    }
}
