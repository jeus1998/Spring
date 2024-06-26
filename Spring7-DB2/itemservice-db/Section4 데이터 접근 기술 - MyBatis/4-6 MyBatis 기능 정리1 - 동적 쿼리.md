# MyBatis 기능 정리1 - 동적 쿼리

### MyBatis 메뉴얼 

- [MyBatis 공식 메뉴얼](https://mybatis.org/mybatis-3/ko/index.html)
- [MyBatis 스프링 공식 메뉴얼](https://mybatis.org/spring/ko/index.html)
- [동적 쿼리에 대한 자세한 내용](https://mybatis.org/mybatis-3/ko/dynamic-sql.html)

### 동적 SQL

- 마이바티스가 제공하는 최고의 기능이자 마이바티스를 사용하는 이유는 바로 동적 SQL 기능 때문
- 동적 쿼리를 위해 제공되는 기능
  - if
  - choose (when, otherwise)
  - trim (where, set)
  - foreach

if
```xml
<select id="findActiveBlogWithTitleLike" resultType="Blog">
 SELECT * FROM BLOG
 WHERE state = ‘ACTIVE’
     <if test="title != null">
     AND title like #{title}
     </if>
</select>
```
- 해당 조건에 따라 값을 추가할지 말지 판단
- 내부의 문법은 ``OGNL``을 사용

choose, when, otherwise
```xml
<select id="findActiveBlogLike" resultType="Blog">
 SELECT * FROM BLOG WHERE state = ‘ACTIVE’
 <choose>
     <when test="title != null">
        AND title like #{title}
     </when>
     <when test="author != null and author.name != null">
        AND author_name like #{author.name}
     </when>
     <otherwise>
        AND featured = 1
     </otherwise>
 </choose>
</select>
```
- 자바의 switch 구문과 유사한 구문도 사용할 수 있다
- 두 when 조건 중 어느 것도 만족하지 않으면 ``AND featured = 1``조건을 추가

trim, where, set
```xml
<select id="findActiveBlogLike" resultType="Blog">
 SELECT * FROM BLOG
 WHERE
     <if test="state != null">
        state = #{state}
     </if>
     <if test="title != null">
        AND title like #{title}
     </if>
     <if test="author != null and author.name != null">
        AND author_name like #{author.name}
     </if>
</select>
```
- 해당 예제는 문제점이 있다. 
- 만약 state가 null 이라면 
  - where AND title like #{title} AND author_name like #{author.name}
  - 이렇게 sql syntax에 오류가 생긴다. 
- 결국 WHERE 문을 언제 넣어야 할지 상황에 따라서 동적으로 달라지는 문제가 있다.
- ```<where>```를 사용하면 이런 문제를 해결할 수 있다.

```<where>```사용
```xml
<select id="findActiveBlogLike" resultType="Blog">
 SELECT * FROM BLOG
 <WHERE>
     <if test="state != null">
        state = #{state}
     </if>
     <if test="title != null">
        AND title like #{title}
     </if>
     <if test="author != null and author.name != null">
        AND author_name like #{author.name}
     </if>
 </WHERE>   
</select>
```
- ``<where>`` 는 문장이 없으면 ``where``를 추가하지 않는다.
- 문장이 있으면 ``where``를 추가한다. 만약 ``and``가 먼저 시작 된다면 ``and``를 지운다.

trim 사용 
```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
 ...
</trim>
```
- 이렇게 정의하면 ```<where>```와 같은 기능을 수행한다

for each
```xml
<select id="selectPostIn" resultType="domain.blog.Post">
     SELECT *
     FROM POST P
     <where>
         <foreach item="item" index="index" collection="list"
             open="ID in (" separator="," close=")" nullable="true">
             #{item}
         </foreach>
     </where>
</select>
```
- 컬렉션을 반복 처리할 때 사용한다. where in (1,2,3,4,5,6) 와 같은 문장을 쉽게 완성할 수 있다.
- 파라미터로 ``List``를 전달하면 된다.
