/**
 * This will dynamically update the title of the page (in the tab)
 * based on the 'title' option of a component
 * https://medium.com/@Taha_Shashtari/the-easy-way-to-change-page-title-in-vue-6caf05006863#:~:text=This%20can%20be%20simply%20done,title%20%3D%20'new%20title'%20.
 */

function getTitle (vm) {
    const { title } = vm.$options
    if (title) {
        return typeof title === 'function'
            ? title.call(vm)
            : title
    }
}

export default {
    created () {
        const title = getTitle(this)
        if (title) {
            document.title = `${process.env.VUE_APP_NAME || 'Wasteless'} · ${title}`
        }
    }
}