package com.musiccollect.data.network

import com.musiccollect.R
import com.musiccollect.data.network.dto.MusicDto

class NetworkDataSource {
    private val mockMusicList = listOf(
        MusicDto("1", "晴天(qingtian)", "周杰伦", "叶惠美", "", 269, "流行", coverRes = R.drawable.cover_yehuimei, audioRes = R.raw.qing_tian),
        MusicDto("2", "七里香(qilixiang)", "周杰伦", "七里香", "", 299, "流行", coverRes = R.drawable.cover_qilixiang, audioRes = R.raw.qi_li_xiang),
        MusicDto("3", "稻香(daoxiang)", "周杰伦", "魔杰座", "", 223, "流行", coverRes = R.drawable.cover_mojiezuo, audioRes = R.raw.dao_xiang),
        MusicDto("4", "花海(huahai)", "周杰伦", "魔杰座", "", 239, "中国风", coverRes = R.drawable.cover_mojiezuo, audioRes = R.raw.hua_hai),
        MusicDto("5", "夜曲(yequ)", "周杰伦", "十一月的萧邦", "", 225, "流行", coverRes = R.drawable.cover_shiyiyuexiaodebang, audioRes = R.raw.ye_qu),
        MusicDto("6", "一路向北(yiluxianbei)", "周杰伦", "J III MP3 Player", "", 215, "流行", coverRes = R.drawable.cover_yiluxiangbei, audioRes = R.raw.yi_lu_xiang_bei),
        MusicDto("8", "光辉岁月(guanghuisuiyue)", "Beyond", "命运派对", "", 295, "摇滚", coverRes = R.drawable.cover_guanghuisuiyue, audioRes = R.raw.guang_hui_sui_yue),
        MusicDto("9", "海阔天空(haikuotiankong)", "Beyond", "乐与怒", "", 326, "摇滚", coverRes = R.drawable.cover_haikuotiankong, audioRes = R.raw.hai_kuo_tian_kong),
        MusicDto("10", "平凡之路(pingfanzhilu)", "朴树", "猎户星座", "", 282, "民谣", audioRes = R.raw.ping_fan_zhi_lu),
        MusicDto("11", "成都(chengdu)", "赵雷", "无法长大", "", 328, "民谣", coverRes = R.drawable.cover_wufazhangda, audioRes = R.raw.cheng_du),
        MusicDto("12", "泡沫(paomo)", "邓紫棋", "Xposed", "", 270, "流行", coverRes = R.drawable.cover_xposed, audioRes = R.raw.pao_mo)
    )

    fun getAllMusic(): List<MusicDto> = mockMusicList

    fun getMusicById(id: String): MusicDto? {
        return mockMusicList.find { it.id == id }
    }

    suspend fun searchMusic(query: String): Result<List<MusicDto>> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return Result.success(mockMusicList)
        val results = mockMusicList.filter {
            it.name.lowercase().contains(q) || it.singer.lowercase().contains(q)
        }
        return Result.success(results)
    }

    suspend fun getMusicList(category: String = "all"): Result<List<MusicDto>> {
        val filtered = if (category == "all") {
            mockMusicList
        } else {
            mockMusicList.filter { it.category == category }
        }
        return Result.success(filtered)
    }
}
