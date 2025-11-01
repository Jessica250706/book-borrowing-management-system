interface Meta {
    permission?: string
    icon?: string
    title: string
}

interface Child {
    permission: string
    path: string
    name: string
    component: string
    icon: string
    meta: {
        title: string
    }
}

export interface MenuOptions {
    path?: string
    meta: Meta
    name: string
    top?: number
    icon: string
    children?: Child[]
}

export interface ThemeConfigProp {
    primary: string
    isGrey: boolean
    isWeak: boolean
}

/* GlobalState */
export interface GlobalState {
    token: string
    userInfo: any
    language: string
    themeConfig: ThemeConfigProp
}

/* MenuState */
export interface MenuState {
    isCollapse: boolean
    menuList: MenuOptions[]
    activeMenu: string
    closeMenuList: string[]
}

/* TabsState */
export interface TabsState {
    tabsMenuValue: string
    tabsMenuList: MenuOptions[]
}

/* AuthState */
export interface AuthState {
    authButtons: {
        [propName: string]: any
    }
    authRouter: string[]
}