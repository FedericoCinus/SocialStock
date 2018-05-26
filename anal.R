#analysis of SocialStock data
library(tidyr)

rm(list = ls())

SocialData = function(title){
  
  str_title = strsplit(title, "[a-z]")
  str_title
  companies = as.integer(str_title[[1]][2])
  range = as.integer(str_title[[1]][3])
  users = as.integer(str_title[[1]][4])
  
  #OPINION MATRIX
  p = read.table(paste(title,".txt" , sep = "") ,sep = ",")
  X = as.data.frame(p)
  days = dim(X)[1] / users
  OPs = list()
  ind = 1
  for(i in 1:days){
    OPs[[i]] = X[ind: (ind + (users-1)),]
    ind = ind +  users 
  }
  
  p = read.table(paste(title,"Inc.txt" , sep = "") ,sep = ",")
  X = as.data.frame(p)
  OPs[["Inc"]] = X
  return(OPs)
}

title = "c5r10n100"
S = SocialData(title)


matplot(S$Inc,type="l", col = c("blue" , "red" , "green"))

legend("topright", legend=c("Crastuli fazione 1", "Non crastuli","Crastuli fazione 2"),
       col=c("blue" , "red" , "green"), pch = c(20,20), cex=0.8)







