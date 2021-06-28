import Vue from 'vue';
import VueRouter, {RouteConfig} from 'vue-router';
import Home from '../views/Home.vue';
import Upload from '../views/Upload.vue';
import About from '../views/About.vue';
import Analysis from '../views/Analysis.vue';
import Tasks from '../views/Tasks.vue';
import Bundle from '../views/Bundle.vue';

Vue.use(VueRouter)

const routes: Array<RouteConfig> = [
    {
        path: '/home',
        name: 'Home',
        component: Home
    },
    {
        path: '/',
        name: 'Home',
        redirect: '/home'
    },
    {
        path: '/upload',
        name: 'Upload',
        component: Upload
    },
    {
        path: '/about',
        name: 'About',
        component: About
    },
    {
        path: '/analysis/:id',
        name: 'Analysis',
        component: Analysis
    },
    {
        path: '/tasks',
        name: 'Tasks',
        component: Tasks
    },
    {
        path: '/bundle/:id',
        name: 'Bundle',
        component: Bundle
    }
]

const router = new VueRouter({
    routes
})

export default router
