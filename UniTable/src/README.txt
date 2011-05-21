Unitable DB Client 0.9

== Requirements: ==
* Sun JRE 1.6

== Supported databases ==
* Oracle
* Mysql (partially)

== Database markup ==
To improve database presentation by Unitable you can use column and table comments (REMARKS in JDBC terms):
1) UNITABLE; - this marker shows that comment must be handled as Unitable markup. Is must present at the beginning of each Unitable comment.
2) HIDDEN; - marked column should not be shown to human (useful for hiding object IDs and other auxiliary columns).
3) HUMAN_NAME=Column Title; - alternate column title, which must be shown to user.
4) HUMAN_FK; - include this column when displaying human-friendly foreign key value.