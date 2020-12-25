interface IExtension {
    name: string
    lang: string
    versionName: string
    iconUrl: string
    installed: boolean
    apkName: string
}

interface ISource {
    id: string
    name: string
    lang: string
    iconUrl: string
    supportsLatest: boolean
    history: any
}

interface IManga {
    title: string
    thumbnailUrl: string
}
