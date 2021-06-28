import axios from 'axios'

class AiBundleService {

    getAIBundles() {
        return axios({
            method: 'get',
            url: process.env.VUE_APP_BACKEND + 'aibundle/getAllBundles',
        }).then(result => {
            return result.data;
        }).catch(() => {
            console.error("Error while attempting to connect to aibundle/getAllBundles");
        })
    }

    getAIBundle(id) {
        return axios.get(process.env.VUE_APP_BACKEND + 'aibundle/getBundle', {
            params: {
                bundleId: id
            }
        }).then(result => {
            return result.data
        }).catch(() => {
            console.error("Error while attempting to connect to aibundle/getBundle");
        })
    }

    deleteAIBundle(id) {
        return axios.delete(process.env.VUE_APP_BACKEND + 'aibundle/deleteBundle', {
            params: {
                bundleId: id
            }
        }).then(result => {
            return result.data
        }).catch(() => {
            console.error("Error while attempting to connect to aibundle/deleteBundle");
        })
    }

}

export default new AiBundleService();