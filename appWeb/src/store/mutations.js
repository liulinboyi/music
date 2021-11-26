const mutations = {
  setPlayingState(state, playing) {
    state.playing = playing
  },
  setSequenceList(state, list) {
    state.sequenceList = list
  },
  setPlaylist(state, list) {
    state.playlist = list
  },
  // 修改当前播放的音乐信息
  setcurrentSongDuration(state, duration) {
    state.playlist[state.currentIndex].duration = duration;
  },
  setcurrentSong(state, list) {
    state.playlist[state.currentIndex] = list;
  },
  setPlayMode(state, mode) {
    state.playMode = mode
  },
  setCurrentIndex(state, index) {
    state.currentIndex = index
  },
  setFullScreen(state, fullScreen) {
    state.fullScreen = fullScreen
  },
  setFavoriteList(state, list) {
    state.favoriteList = list
  },
  addSongLyric(state, {
    song,
    lyric
  }) {
    state.sequenceList.map((item) => {
      if (item.mid === song.mid) {
        item.lyric = lyric
      }
      return item
    })
  },
  setSearchHistory(state, searches) {
    state.searchHistory = searches
  },
  setPlayHistory(state, songs) {
    state.playHistory = songs
  },
  // 设置评论
  setComment(state, comment) {
    state.comment = comment;
  }
}

export default mutations