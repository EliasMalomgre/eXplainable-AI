import Vue from 'vue'

import './styles/quasar.scss'
import '@quasar/extras/material-icons/material-icons.css'
import '@quasar/extras/fontawesome-v5/fontawesome-v5.css'
import {Quasar, Loading, Notify} from 'quasar'

Vue.use(Quasar, {
    plugins: {
        Loading,
        Notify
    },
    config: {
        loading: {

        },
        notify: { /* look at QUASARCONFOPTIONS from the API card (bottom of page) */ }
    },
    extras: [
        'fontawesome-v5',
        'material-icons'
    ]
})