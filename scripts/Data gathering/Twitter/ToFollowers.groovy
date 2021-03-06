/*
FreeTransform, scripts that perform simple Maltego transformations.
Copyright 2017 Mladen Petr

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/*
The script is called upon an arbitrary node.
Parameter is node text.
The scripts runs a search using twitter API and returns first 15 users that are following the user defined in node text.
Results are shown with attached link to twitter profile.
If nothing is found, the user is properly notified.
*/
// @ExecutionModes({on_single_node="node_popup_scripting/FreeTransform/Twitter[addons.installer.title]"})
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder

def CONSUMER_KEY="not_configured"
def CONSUMER_SECRET="not_configured"
def ACCESS_TOKEN_KEY="not_configured"
def ACCESS_TOKEN_SECRET="not_configured"

if (CONSUMER_KEY=="not_configured" || CONSUMER_SECRET=="not_configured" || ACCESS_TOKEN_KEY=="not_configured"  || ACCESS_TOKEN_SECRET=="not_configured"){
	ui.errorMessage("You need to provide valid keys to use this script!")
	return
}

ConfigurationBuilder cb = new ConfigurationBuilder()
cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET).setOAuthAccessToken(ACCESS_TOKEN_KEY).setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET)
TwitterFactory tf = new TwitterFactory(cb.build())
Twitter twitter = tf.getInstance()
def username
try {
	username=node.getPlainText().split('@')[1].split(',')[0]
}
catch(Exception e) {
	ui.errorMessage('The node text is not a twitter username!')
	return
}
def followers
try {
	followers=twitter.getFollowersList(username,-1,15)
}
catch(Exception e) {
	ui.errorMessage('The node text is not a twitter username!')
	return
   }
if (followers.size()==0){
	ui.informationMessage('The user is not following anyone.')
}
for (User follower:followers){
	def child=node.createChild()
	child.setText("@"+follower.getScreenName()+","+follower.getName())
    child.link.text='https://twitter.com/'+follower.getScreenName()
}
