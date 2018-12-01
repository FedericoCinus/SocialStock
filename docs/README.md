# SocialStock
<script src="https://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML" type="text/javascript"></script>
## ToC
- [Overview](#overview)
- [Run](#run)
- [Model](#model)
- [Analysis](#analysis)


## Overview

In this work is introduced the consensus problem on network. In particular we use MAS to model the opinion influence between nodes in a social network and study the time necessary to reach consensus. We introduced the possibility to vary the complexity of each node opinion over a set of fields which we call companies.
Some estimation has been done in order to make the simulation suitable for representing reality and still keep computational costs low. Results show the robustness of the proposed solution both in scale free setting and uniform degree distribution setting.
[More detail on Article.pdf file on the following link: https://github.com/FedericoCinus/SocialStock/blob/master/Article.pdf]


## Run

Download JADE library from http://jade.tilab.com. Clone SocialStock repository from GitHub. Run MainSocial in src directory.


## Model
The initial world's configuration is given by the instantiation of $$N$$ Users. 
The Users live in a world with $$C$$ companies, which are indexed from 0 to $$C-1$$, and are provided with a set of opinions, one for each company.

An opinion is a positive integer which ranges from 0 to $$ R-1 $$, let $$R$$ denote the opinion range. For each User the opinions are stored within a vector $$ \mathbf{v} \in \mathbb{N}^C $$ which we denote as the opinion vector, the entry $$  \mathbf{v}_c $$ is the opinion on the c-th company.

Each User has an inclination $$I \in \{-1,0,1\}$$ whose possible values denote respectively a "bad", "neutral" and "good" averaging opinions across the companies.

Each User has a degree k which is the number of Users to whom he can send messages during the day.

Parameter k is generated randomly according to the scale-free distribution with parameter $$\gamma$$ and is never changed during the simulation; if $$\gamma$$ is set to zero then the degree distribution is uniform.

On each day Users advertize their Inclination and Degree by registering to the Directory Facilitatory (DF); the DF is implemented by Jade and could be compared to the "Yellow Pages" phone book.
Once all the Users have registered to the DF they can make queries in order to look for other Users sharing the same Inclination and then collecting them in a list whose length is equal to their degree. 
Then communication is accomplished through the exchange of messages to Users contained in the list. The messages are implemented by the JADE class ACLMessage and their respective performative is REQUEST.
 
Each time a User receives a message he interacts with the sender.

Let's consider an interacting couple and call the two Users A and B having respectively degree $$k_A$$ and 
$$k_B$$, the interaction consists in these steps :

- Opinion comparison: 
	- B compares its opinion vector $$\mathbf{v}_B$$ with $$\mathbf{v}_A$$, given the subset of companies on which the two Users' opinion are different, a company's index $$c$$ is extracted according to a uniform distribution. If $$\mathbf{v}_A$$ and $\mathbf{v}_B$ are equal the interaction stops. 
- Influence:	
	- The opinion on $$c$$-th company remains unchanged for B while A changes it to B's opinion. This event occurs with probability $$k_B/(k_A + k_B)$$.
	- The opinion on $$c$$-th company remains unchanged for A while B changes it to A's opinion. This event occurs with probability $$k_A/(k_A + k_B)$$.	 		

## Analysis
The analysis has 2 objectives:

- Visualization of the network evolution: red -> inclination=-1, green -> inclination=0, blue -> inclination=1
![gif][gif]

- Finding equilibrium time in inclination vs time plot and opinions vs time plot, with following parameters:
3 companies, 100 users, 20 OpinionRange, scale free network
![inc][inc]
![opi][opi]


We verified that the model complexity is therefore the determinant factor for equilibrium time in network consensus: as the OpinionRange increases the equilibrium time increases too. We also found that scale free setting has a higher equilibrium time measure with respect to the uniform degree distribution setting. Moreover the standard deviation is dependent both on the OpinionRange and the network model setting. (see the article)

[gif]: img/gif2.gif "gif"
[inc]: img/inc.pdf "inc"
[opi]: img/opi.pdf "opi"
