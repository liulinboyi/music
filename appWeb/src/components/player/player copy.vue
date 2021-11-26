<template>
  <div class="player" v-show="playlist.length">
    <transition
      name="normal"
      @enter="enter"
      @after-enter="afterEnter"
      @leave="leave"
      @after-leave="afterLeave"
    >
      <div class="normal-player" v-show="fullScreen">
        <div class="background">
          <img :src="currentSong.pic" />
        </div>
        <div class="top">
          <div class="back" @click="goBack">
            <i class="icon-back"></i>
          </div>
          <h1 class="title">{{ currentSong.name }}</h1>
          <h2 class="subtitle">{{ currentSong.singer }}</h2>
        </div>
        <div
          class="middle"
          @touchstart.prevent="onMiddleTouchStart"
          @touchmove.prevent="onMiddleTouchMove"
          @touchend.prevent="onMiddleTouchEnd"
        >
          <div class="middle-l" :style="middleLStyle">
            <div ref="cdWrapperRef" class="cd-wrapper">
              <div ref="cdRef" class="cd">
                <!-- <img ref="cdImageRef" class="image" :class="cdCls" :src="currentSong.pic" /> -->
                <img
                  ref="cdImageRef"
                  class="image"
                  :class="cdCls"
                  :src="albumimage"
                  v-if="albumimage"
                />
                <img ref="cdImageRef" class="image" :class="cdCls" v-else src="./碟片.png" />
              </div>
            </div>
            <div class="playing-lyric-wrapper">
              <div class="playing-lyric">{{ playingLyric }}</div>
            </div>
          </div>
          <scroll class="middle-r" ref="lyricScrollRef" :style="middleRStyle">
            <div class="lyric-wrapper">
              <div v-if="currentLyric" ref="lyricListRef">
                <p
                  class="text"
                  :class="{ 'current': currentLineNum === index }"
                  v-for="(line,index) in currentLyric.lines"
                  :key="line.num"
                >{{ line.txt }}</p>
              </div>
              <div class="pure-music" v-show="pureMusicLyric">
                <p>{{ pureMusicLyric }}</p>
              </div>
            </div>
          </scroll>
        </div>
        <div class="bottom">
          <div class="dot-wrapper">
            <span class="dot" :class="{ 'active': currentShow === 'cd' }"></span>
            <span class="dot" :class="{ 'active': currentShow === 'lyric' }"></span>
          </div>
          <div class="progress-wrapper">
            <span class="time time-l">{{ formatTime(currentTime) }}</span>
            <div class="progress-bar-wrapper">
              <progress-bar
                ref="barRef"
                :progress="progress"
                @progress-changing="onProgressChanging"
                @progress-changed="onProgressChanged"
              ></progress-bar>
            </div>
            <span class="time time-r">{{ formatTime(currentSong.duration) }}</span>
          </div>
          <div class="operators">
            <div class="icon i-left">
              <i @click="changeMode" :class="modeIcon"></i>
            </div>
            <div class="icon i-left" :class="disableCls">
              <i @click="prev" class="icon-prev"></i>
            </div>
            <div class="icon i-center" :class="disableCls">
              <i @click="togglePlay" :class="playIcon"></i>
            </div>
            <div class="icon i-right" :class="disableCls">
              <i @click="next" class="icon-next"></i>
            </div>
            <div class="icon i-right">
              <i @click="toggleFavorite(currentSong)" :class="getFavoriteIcon(currentSong)"></i>
            </div>
          </div>
          <div class="operators other-operator">
            <div class="icon comment">
              <div @click="showComment">评论</div>
            </div>
            <div class="icon downliad">
              <div>下载</div>
            </div>
          </div>
        </div>
      </div>
    </transition>
    <mini-player :progress="progress" :toggle-play="togglePlay"></mini-player>
    <audio
      ref="audioRef"
      @pause="pause"
      @canplay="ready"
      @error="error"
      @timeupdate="updateTime"
      @ended="end"
    ></audio>
  </div>
</template>

<script>
import { useStore } from 'vuex'
import { computed, watch, ref, nextTick } from 'vue'
import useMode from './use-mode'
import useFavorite from './use-favorite'
import useCd from './use-cd'
import useLyric from './use-lyric'
import useMiddleInteractive from './use-middle-interactive'
import useAnimation from './use-animation'
import usePlayHistory from './use-play-history'
import ProgressBar from './progress-bar'
import Scroll from '@/components/base/scroll/scroll'
import MiniPlayer from './mini-player'
import { formatTime } from '@/assets/js/util'
import { PLAY_MODE, CURRENT_SONG, ALBUM_KEY, RESOURCE_BASE_URL } from '@/assets/js/constant'
import storage from 'good-storage'
import { onBeforeRouteLeave, onBeforeRouteUpdate, useRouter, useRoute } from 'vue-router'

export default {
  name: 'player',
  components: {
    MiniPlayer,
    ProgressBar,
    Scroll
  },
  setup() {
    // onBeforeRouteLeave((to, from) => {
    //   // 监听路由即将离开
    //   console.log("我监听到了浏览器的返回按钮事件啦")
    //   if (fullScreen.value) { // 当数据还在加载时，阻止路由跳转
    //     goBack() // 监听到返回上一页事件后，将播放器隐藏
    //     return false
    //   } else {
    //     return true
    //   }
    // })

    // data
    const audioRef = ref(null)
    const barRef = ref(null)
    const songReady = ref(false)
    const currentTime = ref(0)
    const musicCache = ref(null)
    const albumimage = ref(null)
    let progressChanging = false

    // vue-router
    const router = useRouter()
    const route = useRoute()
    // vuex
    const store = useStore()
    const fullScreen = computed(() => store.state.fullScreen)
    const currentSong = computed(() => {
      return store.getters.currentSong
    })
    const playing = computed(() => store.state.playing)
    const currentIndex = computed(() => store.state.currentIndex)
    const playMode = computed(() => store.state.playMode)

    // hooks
    const { modeIcon, changeMode } = useMode()
    const { getFavoriteIcon, toggleFavorite } = useFavorite()
    const { cdCls, cdRef, cdImageRef } = useCd()
    const { currentLyric, currentLineNum, pureMusicLyric, playingLyric, lyricScrollRef, lyricListRef, playLyric, stopLyric } = useLyric({
      songReady,
      currentTime
    })
    const { currentShow, middleLStyle, middleRStyle, onMiddleTouchStart, onMiddleTouchMove, onMiddleTouchEnd } = useMiddleInteractive()
    const { cdWrapperRef, enter, afterEnter, leave, afterLeave } = useAnimation()
    const { savePlay } = usePlayHistory()

    // computed
    const playlist = computed(() => store.state.playlist)

    const playIcon = computed(() => {
      return playing.value ? 'icon-pause' : 'icon-play'
    })

    const progress = computed(() => {
      return currentTime.value / currentSong.value.duration
    })

    const disableCls = computed(() => {
      return songReady.value ? '' : 'disable'
    })

    setTimeout(() => {

      // 暂时废弃这种做法，使用vue-router提供的组件内路由生命周期
      // 监听浏览器返回上一页事件
      // window.addEventListener("popstate", (e) => {
      //   console.log("我监听到了浏览器的返回按钮事件啦")
      //   if (fullScreen.value) { // 如果在全屏展示则只隐藏，不返回
      //     e.preventDefault()
      //     e.stopPropagation()
      //   }
      //   goBack() // 监听到返回上一页事件后，将播放器隐藏
      // }, false);

      console.log(audioRef.value)

      let au = audioRef.value;
      if (au && !store.state.playing/*正在播放则不进入此分支*/) {
        // Once the metadata has been loaded, display the duration in the console
        au.addEventListener('loadedmetadata', (e) => {
          // 加载音频资源后，将progressChanging置为true，这会导致，进度条触发事件回调函数失效
          progressChanging = true
          const audioEl = audioRef.value
          audioEl.muted = true // 先静音
          setTimeout(() => {
            // 小米浏览器的Bug,导致必须等待500ms才能获取音频时长，其实这个最好在后端就能获取音频时长
            // 但是即使在后端获取时长，为了做进度本地保存与恢复也得做特殊处理，来解决小米浏览器的Bug引起的问题
            // Obtain the duration in seconds of the audio file (with milliseconds as well, a float value)
            let duration = e.target.duration;
            console.log("音频时长", e.target, duration) // 单位秒
            // store.commit("setcurrentSongDuration", duration)
            let temp = { ...currentSong.value }
            temp.duration = duration
            store.commit("setcurrentSong", temp)

            // setTimeout(() => {
            //   progressChanging = false
            //   audioEl.muted = false // 开音效
            //   let cache = storage.session.get(CURRENT_SONG)
            //   // debugger
            //   if (cache && cache.url === currentSong.value.url) { // 如果缓存的音频和新的音频相同则进入此分支
            //     const audioEl = audioRef.value
            //     if (cache) { // 如果有缓存
            //       console.log("cache", cache)
            //       // currentTime是一个可读兼可写的属性，用来设置或获取当前已经播放的时长，单位是秒。
            //       audioEl.currentTime = cache.currentTime
            //     }
            //   }
            // })
          }, 500)

        }, false);

        // au.addEventListener("timeupdate", (e) => {
        //   if (currentSong.value.duration) {
        //     return
        //   }
        //   let duration = e.target.duration
        //   console.log("timeupdate", duration)
        //   let temp = { ...currentSong.value }
        //   temp.duration = duration
        //   store.commit("setcurrentSong", temp)
        // })
      }

    })

    // 暂时使用专辑封面
    watch(currentSong, () => {
      let temp = storage.session.get(ALBUM_KEY)
      console.log("state", store.state)
      if (temp) {
        console.log("albumimage", temp)
        albumimage.value = `${RESOURCE_BASE_URL}${temp.img}`
      }


      // let audio = new Audio()
      // // 将本地的音频地址给这个空的音频实例对象，从而可以得到我们需要的数据。
      // try {
      //   // debugger
      //   let url = store.state.playlist[store.state.currentIndex].url
      //   console.log("url", url)
      //   audio.src = url
      // } catch {
      // }
      // audio.muted = true
      // // audio.play().then(() => audio.pause())
      // audio.addEventListener('loadedmetadata', function () {
      //   setTimeout(() => {
      //     console.log('音频加载成功', audio.duration)
      //   }, 500)
      // })
      // // audio.muted = false
      // audio.oncanplay = function () { }
      // audio.onerror = function (e) {
      //   console.log(e)
      //   console.log('音频加载失败')
      // }


    })

    // watch
    watch(currentSong, (newSong, oldSong) => {
      console.log(newSong, oldSong)
      if (!newSong.id || !newSong.url) {
        return
      }

      let cache = storage.session.get(CURRENT_SONG)
      if (cache && cache.url === newSong.url) { // 如果缓存的音频和新的音频相同则进入此分支
        setTimeout(() => {
          const audioEl = audioRef.value
          progressChanging = false
          audioEl.muted = false // 开音效
          // debugger
          console.log("cache", cache)
          // currentTime是一个可读兼可写的属性，用来设置或获取当前已经播放的时长，单位是秒。
          audioEl.currentTime = cache.currentTime
          currentTime.value = cache.currentTime
        })
      }


      if (oldSong.url !== newSong.url) {
        // 获取缓存中的播放进度
        currentTime.value = 0
        songReady.value = false
        const audioEl = audioRef.value
        console.log("watch currentSong", audioEl.duration)
        audioEl.src = newSong.url
        audioEl.play()
        store.commit('setPlayingState', true)
      }
    })

    watch(playing, (newPlaying) => {
      if (!songReady.value) {
        return
      }
      const audioEl = audioRef.value
      if (newPlaying) {
        audioEl.play()
        playLyric()
      } else {
        audioEl.pause()
        stopLyric()
      }
    })

    watch(fullScreen, async (newFullScreen) => {
      if (newFullScreen) {
        await nextTick()
        barRef.value.setOffset(progress.value)
      }
    })

    // methods

    async function showComment() {
      console.log("showComment")
      // store.commit("setComment", [1, 2, 3])
      await router.push({
        name: `comment`,
        query: {
          id: currentSong.value.id
        }
      })
    }

    function goBack() {
      store.commit('setFullScreen', false)
    }

    function togglePlay() {
      if (!songReady.value) {
        return
      }
      console.log("togglePlay")
      store.commit('setPlayingState', !playing.value)
    }

    function pause() {
      // 小米浏览器音频播放也会执行暂停回调，真奇葩！
      if (!playing.value) { // 如果playing此时为false则退出
        return
      }
      console.log("pause")
      // store.commit('setPlayingState', false)
    }

    function prev() {
      const list = playlist.value
      if (!songReady.value || !list.length) {
        return
      }

      if (list.length === 1) {
        loop()
      } else {
        let index = currentIndex.value - 1
        if (index === -1) {
          index = list.length - 1
        }
        store.commit('setCurrentIndex', index)
      }
    }

    function next() {
      const list = playlist.value
      if (!songReady.value || !list.length) {
        return
      }

      if (list.length === 1) {
        loop()
      } else {
        let index = currentIndex.value + 1
        if (index === list.length) {
          index = 0
        }
        store.commit('setCurrentIndex', index)
      }
    }

    function loop() {
      const audioEl = audioRef.value
      audioEl.currentTime = 0
      audioEl.play()
      store.commit('setPlayingState', true)
    }

    function ready() {
      if (songReady.value) {
        return
      }
      songReady.value = true
      playLyric()
      savePlay(currentSong.value)
    }

    function error() {
      songReady.value = true
    }

    // 音频进度条更新
    function updateTime(e) {
      if (!progressChanging) {
        currentTime.value = e.target.currentTime
        // console.log(currentTime.value)
        // currentSong


        // 缓存播放进度
        let temp = { ...currentSong.value }
        temp.currentTime = currentTime.value
        // console.log(temp)
        // store.commit("setcurrentSong", temp)

        storage.session.set(CURRENT_SONG, temp)
      }
    }

    function onProgressChanging(progress) {
      progressChanging = true
      currentTime.value = currentSong.value.duration * progress
      playLyric()
      stopLyric()
    }

    function onProgressChanged(progress) {
      audioRef.value.currentTime = currentTime.value = currentSong.value.duration * progress
      console.log("onProgressChanged", playing.value)
      if (!playing.value) {
        store.commit('setPlayingState', true)
        progressChanging = false
      }
      playLyric()
    }

    function end() {
      currentTime.value = 0
      if (playMode.value === PLAY_MODE.loop) {
        loop()
      } else {
        next()
      }
    }

    return {
      audioRef,
      barRef,
      fullScreen,
      currentTime,
      currentSong,
      playlist,
      playIcon,
      disableCls,
      progress,
      showComment,
      goBack,
      togglePlay,
      pause,
      prev,
      next,
      ready,
      error,
      updateTime,
      formatTime,
      onProgressChanging,
      onProgressChanged,
      end,
      // mode
      modeIcon,
      changeMode,
      // favorite
      getFavoriteIcon,
      toggleFavorite,
      // cd
      cdCls,
      cdRef,
      cdImageRef,
      // lyric
      currentLyric,
      currentLineNum,
      pureMusicLyric,
      playingLyric,
      lyricScrollRef,
      lyricListRef,
      // middle-interactive
      currentShow,
      middleLStyle,
      middleRStyle,
      onMiddleTouchStart,
      onMiddleTouchMove,
      onMiddleTouchEnd,
      // animation
      cdWrapperRef,
      enter,
      afterEnter,
      leave,
      afterLeave,
      albumimage
    }
  }
}
</script>

<style lang="scss" scoped>
.player {
  .normal-player {
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    z-index: 150;
    background: $color-background;
    .background {
      position: absolute;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      z-index: -1;
      opacity: 0.6;
      filter: blur(20px);

      img {
        width: 100%;
        height: 100%;
      }
    }
    .top {
      position: relative;
      margin-bottom: 25px;
      .back {
        position: absolute;
        top: 0;
        left: 6px;
        z-index: 50;
      }
      .icon-back {
        display: block;
        padding: 9px;
        font-size: $font-size-large-x;
        color: $color-theme;
        transform: rotate(-90deg);
      }
      .title {
        width: 70%;
        margin: 0 auto;
        line-height: 40px;
        text-align: center;
        @include no-wrap();
        font-size: $font-size-large;
        color: $color-text;
      }
      .subtitle {
        line-height: 20px;
        text-align: center;
        font-size: $font-size-medium;
        color: $color-text;
      }
    }
    .middle {
      position: fixed;
      width: 100%;
      top: 80px;
      bottom: 170px;
      white-space: nowrap;
      font-size: 0;
      .middle-l {
        display: inline-block;
        vertical-align: top;
        position: relative;
        width: 100%;
        height: 0;
        padding-top: 80%;
        .cd-wrapper {
          position: absolute;
          left: 10%;
          top: 0;
          width: 80%;
          box-sizing: border-box;
          height: 100%;
          .cd {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            img {
              position: absolute;
              left: 0;
              top: 0;
              width: 100%;
              height: 100%;
              box-sizing: border-box;
              border-radius: 50%;
              border: 10px solid rgba(255, 255, 255, 0.1);
            }
            .playing {
              animation: rotate 20s linear infinite;
            }
          }
        }
        .playing-lyric-wrapper {
          width: 80%;
          margin: 30px auto 0 auto;
          overflow: hidden;
          text-align: center;
          .playing-lyric {
            height: 20px;
            line-height: 20px;
            font-size: $font-size-medium;
            color: $color-text-l;
          }
        }
      }
      .middle-r {
        display: inline-block;
        vertical-align: top;
        width: 100%;
        height: 100%;
        overflow: hidden;
        .lyric-wrapper {
          width: 80%;
          margin: 0 auto;
          overflow: hidden;
          text-align: center;
          .text {
            line-height: 32px;
            color: $color-text-l;
            font-size: $font-size-medium;
            &.current {
              color: $color-text;
            }
          }
          .pure-music {
            padding-top: 50%;
            line-height: 32px;
            color: $color-text-l;
            font-size: $font-size-medium;
          }
        }
      }
    }
    .bottom {
      position: absolute;
      bottom: 50px;
      width: 100%;
      .dot-wrapper {
        text-align: center;
        font-size: 0;
        .dot {
          display: inline-block;
          vertical-align: middle;
          margin: 0 4px;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: $color-text-l;
          &.active {
            width: 20px;
            border-radius: 5px;
            background: $color-text-ll;
          }
        }
      }
      .progress-wrapper {
        display: flex;
        align-items: center;
        width: 80%;
        margin: 0px auto;
        padding: 10px 0;
        .time {
          color: $color-text;
          font-size: $font-size-small;
          flex: 0 0 40px;
          line-height: 30px;
          width: 40px;
          &.time-l {
            text-align: left;
          }
          &.time-r {
            text-align: right;
          }
        }
        .progress-bar-wrapper {
          flex: 1;
        }
      }
      .other-operator {
        padding: 10px 0;
      }
      .operators {
        display: flex;
        align-items: center;
        .icon {
          flex: 1;
          color: $color-theme;
          &.disable {
            color: $color-theme-d;
          }
          i {
            font-size: 30px;
          }
        }
        .comment {
          display: flex;
          justify-content: center;
        }
        .downliad {
          display: flex;
          justify-content: center;
        }
        .i-left {
          text-align: right;
        }
        .i-center {
          padding: 0 20px;
          text-align: center;
          i {
            font-size: 40px;
          }
        }
        .i-right {
          text-align: left;
        }
        .icon-favorite {
          color: $color-sub-theme;
        }
      }
    }
    &.normal-enter-active,
    &.normal-leave-active {
      transition: all 0.6s;
      .top,
      .bottom {
        transition: all 0.6s cubic-bezier(0.45, 0, 0.55, 1);
      }
    }
    &.normal-enter-from,
    &.normal-leave-to {
      opacity: 0;
      .top {
        transform: translate3d(0, -100px, 0);
      }
      .bottom {
        transform: translate3d(0, 100px, 0);
      }
    }
  }
}
</style>
