# 森罗物语：群峦厨房
### https://www.curseforge.com/minecraft/mc-mods/kaleidoscope-tfc-cookery

## KubeJS 兼容 (自定义汤底)
其实，在森罗物语本身就有这个自定义汤底的兼容，但是它只能支持原版的桶

在本模组中，0.2.1版本给它加上了可以使用群峦桶的兼容
```JavaScript
StartupEvents.postInit(event => {
    KCookery.registerSoupBase(
        // .create( ResourceLocation_ ) 严格使用流体id
        KCookerySoupBase.create('minecraft:milk') 
        
        // .bubbleColor( int )  气泡颜色
        .bubbleColor(0xffffff)
        
        // .soupBaseTexture( ResourceLocation_ ) 锅底的流体贴图
        .soupBaseTexture('minecraft:block/glass') 
        
        // 以下两个是可选项，如果你不打算用原版桶的话可以不写
        .returnContainerFunction((level, entity, item) => {
            return Item.of('minecraft:bucket') // 返回的原版桶
        })
        .returnSoupBaseFunction((level, entity, item) => {
            return Item.of('minecraft:milk_bucket') // 返回的原版奶桶
        })
    )
})
```

## 修改数据（茶药水效果，食物药水效果，可装入油壶的油）
可以使用数据包的形式进行修改。具体操作就是覆盖```data/ktfcc/ktfcc/tea_effect/``` ```data/ktfcc/ktfcc/food_effect/``` ```data/ktfcc/ktfcc/oil/```里的内容。