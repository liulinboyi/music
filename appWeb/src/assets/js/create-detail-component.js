import MusicList from '@/components/music-list/music-list'
import storage from 'good-storage'
import axios from 'axios'
import {
  ALBUM_KEY,
  RESOURCE_BASE_URL,
  BASR_URL
} from '@/assets/js/constant'

export default function createDetailComponent(name, key, fetch) {
  return {
    name,
    components: {
      MusicList
    },
    props: {
      data: Object
    },
    data() {
      return {
        songs: [],
        loading: true,
        temppic: ""
      }
    },
    computed: {
      computedData() {
        let ret = null
        const data = this.data
        if (data) {
          ret = data
        } else {
          const cached = storage.session.get(key)
          if (cached && (cached.mid || cached.id + '') === this.$route.params.id) {
            ret = cached
          }
        }
        return ret
      },
      pic: {
        get() {
          // 封面
          if (this.temppic) {
            return this.temppic
          }
          const data = this.computedData
          console.log("create-detail-component -> pic()", data)
          // return data && data.pic
          let pic = data && data.img;
          if (pic) {
            pic = `${RESOURCE_BASE_URL}${pic}`
            console.log(pic)
            return pic;
          } else {
            return null
          }

        },
        set(value) {
          this.temppic = value
        }
      },
      title() {
        const data = this.computedData
        // return data && (data.name || data.title)
        return data && (data.title || data.content)
      }
    },
    async created() {
      // const data = this.computedData
      // if (!data) {
      //   const path = this.$route.matched[0].path
      //   this.$router.push({
      //     path
      //   })
      //   return
      // }
      // const result = await fetch(data)
      // this.songs = await processSongs(result.songs)
      const cached = storage.session.get(key)
      if (cached && cached.allMedia) {
        for (let item of cached.allMedia) {
          item.url = `${RESOURCE_BASE_URL}${item.path}`
          let patten = item.fileName.match(/([^+]+)\.([^+]+)/)
          if (patten) {
            item.fileName = patten[1]
          }
          // match(/(?:[^+]+)\.([^+]+)/)
          // item.duration = item.size / 1000 / 10
        }
        this.songs = cached.allMedia
        console.log(this.songs)
      } else {
        // 没有缓存，从服务器请求数据
        let result = await axios({
          method: "get",
          url: `${BASR_URL}/personalColumn/findDetailById?id=${this.$route.params.id}`,
          withCredentials: true,
        })
        if (result.data.success) {
          console.log(result.data.data)

          let cached = result.data.data
          for (let item of cached.allMedia) {
            item.url = `${RESOURCE_BASE_URL}${item.path}`
            let patten = item.fileName.match(/([^+]+)\.([^+]+)/)
            if (patten) {
              item.fileName = patten[1]
            }
            // match(/(?:[^+]+)\.([^+]+)/)
            // item.duration = item.size / 1000 / 10
          }
          this.songs = cached.allMedia
          this.pic = `${RESOURCE_BASE_URL}${cached.img}`
          console.log(this.songs)

          storage.session.set(ALBUM_KEY, cached)
        }
      }

      this.loading = false
    }
  }
}