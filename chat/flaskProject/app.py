from flask import Flask,render_template,request
import pymysql
from sklearn.feature_extraction.text import CountVectorizer
import numpy as np
from scipy.linalg import norm
app = Flask(__name__)

@app.route("/chat",methods=["GET","POST"])
def chat():  # put application's code here
    # if request.method=="GET":
    #     return render_template("chat.html")
    # request.form.get("user")
    print(request.path)
    question = request.args["question"].encode("iso-8859-1").decode('gbk')
    print(question)
    #连接mysql
    conn=pymysql.connect(host="127.0.0.1",port=3306,user='root',password="666666",charset='utf8',db='mybatis')
    cursor=conn.cursor(cursor=pymysql.cursors.DictCursor)

    #发送指令
    sql="select question,answer from covid19_qas"
    cursor.execute(sql)
    datalist=cursor.fetchall()
    qlist=[]
    alist=[]
    matchlist=[]
    for i in datalist:
        qlist.append(i['question'])
        matchlist.append(tf_similarity(question,i['question']))
        alist.append(i['answer'])
    print(qlist)
    print(alist)
    print(matchlist)
    index=matchlist.index(max(matchlist))
    print(question)
    print(index)
    print(str(index)+" "+qlist[index]+":"+alist[index])
    #关闭
    cursor.close()
    conn.close()
    return alist[index]

@app.route("/insert",methods=["GET","POST"])
def insert():  # put application's code here
    # if request.method=="GET":
    #     return render_template("chat.html")
    # request.form.get("user")
    #连接mysql
    conn=pymysql.connect(host="127.0.0.1",port=3306,user='root',password="666666",charset='utf8',db='mybatis')
    cursor=conn.cursor(cursor=pymysql.cursors.DictCursor)

    q="我是呆逼"
    a="没错你是"
    #发送指令
    sql="insert into covid19_qas(question,answer) values (%s,%s)"
    cursor.execute(sql,[q,a])
    conn.commit()

    #关闭
    cursor.close()
    conn.close()
    return "插入成功"

def tf_similarity(s1, s2):
    def add_space(s):
        return ' '.join(list(s))

    s1, s2 = add_space(s1), add_space(s2) #在字中间加上空格
    cv = CountVectorizer(tokenizer=lambda s: s.split()) #转化为TF矩阵
    corpus = [s1, s2]
    vectors = cv.fit_transform(corpus).toarray() #计算TF系数
    return np.dot(vectors[0], vectors[1]) / (norm(vectors[0]) * norm(vectors[1]))

if __name__ == '__main__':
    app.run()

