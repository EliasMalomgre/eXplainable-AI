import axios from 'axios'

class TaskService {

    getTasksForUpload(bundleId) {
        return axios({
            method: 'get',
            url: process.env.VUE_APP_BACKEND + 'task/getTasksForBundle?bundleId=' + bundleId,
        }).then(result => {
            return result.data.tasks;
        }).catch(() => {
            console.error("Error while attempting to connect to task/getTasksForBundle");
        })
    }

    getAllTasks() {
        return axios({
            method: 'get',
            url: process.env.VUE_APP_BACKEND + 'task/getAllTasks',
        }).then(result => {
            return result.data.tasks;
        }).catch(() => {
            console.error("Error while attempting to connect to task/getAllTasks");
        })
    }

    deleteTasksForAIBundle(id) {
        return axios.delete(process.env.VUE_APP_BACKEND + 'task/deleteTasksForBundle', {
            params: {
                bundleId: id
            }
        }).then(result => {
            return result.data
        }).catch(() => {
            console.error("Error while attempting to connect to task/deleteTasksForBundle");
        })
    }

    deleteTask(id) {
        return axios.delete(process.env.VUE_APP_BACKEND + 'task/deleteTask', {
            params: {
                taskId: id
            }
        }).then(result => {
            return result.data
        }).catch(() => {
            console.error("Error while attempting to connect to task/deleteTask");
        })
    }

    cancelTask(id){
        return axios({
            method: 'post',
            url: process.env.VUE_APP_BACKEND + 'task/cancelTask',
            params: {
                taskId: id
            },
            headers: {'Content-Type': 'application/json'}
        }).then().catch(err => {
            console.error(`Error while attempting to connect to task/cancelTask with id=${id}`);
        })
    }
}

export default new TaskService();