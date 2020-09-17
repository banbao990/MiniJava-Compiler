# Attention

[TOC]



## 1. 说明

+ `D(desperate)` 表示不使用这种方法



## 2. Kanga 语法

+ 支持 `C` 风格的注释

+ `Label` 是**全局**的

+ 寄存器
    + `a0 - a3` 函数调用参数传递
    + `v0` 作为返回值
    + `v0 - v1` 也可以作为临时变量使用
    + `s0 - s7` 局部变量(函数调用时需要保存)
    + `t0 - t9` 临时结果(函数调用时不需要保存)

+ `first value at (SPILLEDARG 0)`
+ 函数调用时参数多于 `4` 个
    + `you need to use the PASSARG stmt` (保存到栈)
    + `PASSARG` 是从 `1` 开始，`PASSARG i` 需要用 `SPILLEDARG i-1` 访问



## 3. 大体思路

+ `Procedure` 是活性分析的基本单元
+ `Stmt` 是每一条语句
+ 为每一个 `Procedure` 构造好 `Stmt` 的变量表
    + 初步设想是按照自然顺序用 `ArrayList` 保存即可



## 4. 第一次 visitor

+ `1.` 为每一个 `Procedure` 维护一个 `old Label -> new Lable` 的映射
    + `D` `(1)` 对于 `Procedure` 的函数名 `Label` 我们直接不 `accept` 即可
    + `D` `(2)` `Call` 不好处理
    + `D` `(1)` 还是只处理 `CJUMP` 和 `JUMP` 比较简单
    + `D` `(2)` 不需要考虑 `Stmt` 里面的 `Label`, 因为如果跳转用不到就没用了
    + `(1)` 具体实现见`4-(1)-[2]`

+ `2.` 为每一个 `Procedure` 构造好 `Stmt` 列表

+ `3.` 为每一个 `Stmt` 构造好变量列表

+ `4.` `Stmt` 加上后继
    + `(1)` 关于 `JUMP/CJUMP` 等的后继处理方法是找到原来的 `Label` 处在第几个 `Stmt`
        + `[1]` 实现方法 `1` : 多加一次 `visitor`
        + `[2]` 实现方法 `2` : 在进入 `Procedure` 的时候我们就进行一次关于关于 `Label` 在第几行的 `HashMap`
            + 此时我们也可以在这里将 `Label` 的映射给完成了

+ `5.` 需要为后继是 `exit` 的 `stmt` 进行修正
    + 不需要 (已经没有复合语句 `if(...){...}else{...}` 等)

+ `6.` 生成 `id/vars`
    + 使用对 `Temp` 屏蔽的方法,能够到达`visit(Temp)` 的必然都是 `vars` ，屏蔽 `id`



## 5. Global

+ 规则推导

```java
/**
 * 哪一种类型(需要针对具体的 Stmt 进行分析)
 * NoOpStmt()       0
 * ErrorStmt()      0
 * CJumpStmt()      4
 * JumpStmt()       0
 * HStoreStmt()     4
 * HLoadStmt()      4
 * MoveStmt()       2
 * PrintStmt()      4
 * exit             1
 *
 * join(v) = U Constraints(w:w是v的后继)
 *
 *  0 : 其他类型 Constraints(v) = join(v)
 *  1 : 退出结点 Constraints(v) = {}
 *  2 : 赋值语句 Constraints(v) = join(v) - id + vars
 *  3 : 变量声明 Constraints(v) = join(v) - id
 *  4 : 条件语句 Constraints(v) = join(v) + vars
 */
```

+ `case 3` 没有显式存在过(和赋值一起出现)
    + 有的，函数参数，现在的处理方式直接加在第一句上
  + 是否需要添加一条语句，或者说直接把声明语句和第一句加在一起是否会出错
   + `[1]` 前 `4` 个变量无需分配寄存器(第一次 `visitor`)
  + `[2]` 在生成代码的时候要注意并不是简单 `get`，而是要从栈中读取
+  我们发现这些都可以使用 Constraints(v) = join(v) - id + vars 来解决
 + 只是有的时候某些集合为空
+ 生成每一个变量的活跃区间
+ 排序
+ 寄存器分配(线性扫描)
+ 函数名和跳转Label的判重
 + 没有实现
+ 具体实现可以先加一个 visitor 加入所有的函数名, 然后在分配Label时检查是否重名



## 6. other

+ 尝试屏蔽所有 `Label`
+ 好像解决不了单个变量复用的问题



## 7. 感想

+ 直接使用 `JTB` 真的香(很多信息可以从 `n` 中获取,不需要 `accept`)



## 8. 以下问题怎么解决?(就这里而言好像并没有问题)

```java
int x, y;   // {}
y = 1;      // {}
x = y;      // y
x = 2;      // y
x = y;      // y
print(x)    // x
```



## 9. 命令行
```shell
java Main && java -jar ../kgi.jar < test.kg & pause & java -jar ../pgi.jar < test.spg
```



## 10. 其他

+  是否需要预留出参数传递的 `SPILLEDARG`
    + 比如 `A` 调用`B`，`B`的参数有 `20` 个(栈传递 `16` 个)
    + 是否需要在B函数开头预留出 `16` 个栈空间
    + `ans`：需要,若无分配寄存器,此时就是用之前的栈空间(否则会覆盖)

