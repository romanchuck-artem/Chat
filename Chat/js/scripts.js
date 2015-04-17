	
var uniqueId = function() {
	var date = Date.now();
	var random = Math.random() * Math.random();

	return Math.floor(date * random).toString();
};

var theMessage = function(text,name, done) {
	return {
		msg:text,
		nick: name,
		status: done,
		id: uniqueId()
	};
};

var messageList = [];

function run(){

	var allTasks = restore() || [];
	createAllTasks(allTasks);
}

function createAllTasks(allTasks) {
	for(var i = 0; i < allTasks.length; i++){
    	var incoming = document.getElementById('incoming_messages');
		var p = document.createElement('p');
		p.className += 'message';
		p_msg = document.createElement('p');
		p_msg.className += 'text_msg';
		nick_space = document.createElement('span');
		nick_space.className += 'nick';
		p_buttons = document.createElement('span');
		p_buttons.className += 'buttons';
		p_buttons.setAttribute('style', 'float: right');
		p_msg.innerHTML = allTasks[i].msg + "</br>";
    	nick_space.innerHTML = allTasks[i].nick;
    	var edit_text = document.createElement('button');

		messageList.push(theMessage(allTasks[i].msg,allTasks[i].nick, true));
		store(messageList);
		edit_text.onclick = function()
		{
			message_area.value = message;
			end_edit = document.getElementById('end_edit');
			end_edit.style.display = 'inline';
		    end_edit.onclick = function () 
			{
				p_msg.childNodes[0].data = message_area.value;
				end_edit.style.display = 'none';
				message_area.value = clean_message;
			};
		};
		var edit_img = document.createElement('img');
		edit_img.src = "edit_img.png";
		var delete_text = document.createElement('button');
    	delete_text.onclick = function() //todo
		{
			var parent = p.parentElement;
			parent.removeChild(p);
		};
		var delete_img = document.createElement('img');
		delete_img.src = "delete_img.png";
		incoming.appendChild(p);
		p_buttons.appendChild(edit_text);
		p_buttons.appendChild(delete_text);
		p.appendChild(nick_space);
		p.appendChild(p_buttons);
		p.appendChild(p_msg);
		edit_text.appendChild(edit_img);
		delete_text.appendChild(delete_img);
	}
}

function store(listToSave) {

	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	localStorage.setItem("Chat message", JSON.stringify(listToSave));
}

function restore() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	var item = localStorage.getItem("Chat message");

	return item && JSON.parse(item);
}
var login, online = false;

function sent()
{
	if(online)
	{
		var message = document.getElementById('message_area').value;
		var message_area = document.getElementById('message_area'),
		clean_message = ' ';
    	message_area.value = clean_message;
		var incoming = document.getElementById('incoming_messages');
		var p = document.createElement('p');
		p.className += 'message';
		p_msg = document.createElement('p');
		p_msg.className += 'text_msg';
		nick_space = document.createElement('span');
		nick_space.className += 'nick';
		p_buttons = document.createElement('span');
		p_buttons.className += 'buttons';
		p_buttons.setAttribute('style', 'float: right');
    	p_msg.innerHTML = message + "</br>";
    	nick_space.innerHTML = login;
		var edit_text = document.createElement('button');
		messageList.push(theMessage(message,login, true)); 	
		store(messageList);
		edit_text.onclick = function()
		{
			message_area.value = message;
			end_edit = document.getElementById('end_edit');
			end_edit.style.display = 'inline';
			end_edit.onclick = function () 
			{
				p_msg.childNodes[0].data = message_area.value;
				end_edit.style.display = 'none';
				message_area.value = clean_message;
			};
		};
		var edit_img = document.createElement('img');
		edit_img.src = "edit_img.png";
		var delete_text = document.createElement('button');
    	delete_text.onclick = function() //todo
		{
			var parent = p.parentElement;
			parent.removeChild(p);
		};
		var delete_img = document.createElement('img');
		delete_img.src = "delete_img.png";
		incoming.appendChild(p);
		p_buttons.appendChild(edit_text);
		p_buttons.appendChild(delete_text);
		p.appendChild(nick_space);
		p.appendChild(p_buttons);
		p.appendChild(p_msg);
		edit_text.appendChild(edit_img);
		delete_text.appendChild(delete_img);
    }
    else
    	alert("Please, login!");
}
 function log()
{
	if(!online)
	{
		login = document.getElementById('name').value;
		if(login != "")
		{
			if(submit(login))
			{
				online = true;
				var log_window = document.getElementById('login');
				log_window.style.display = 'none';
				var exit_window = document.getElementById('exit');
				exit_window.style.display = 'inline';
				var text_exit = document.getElementById('text_exit');
				var text = document.createTextNode("You have been logged as " + login);
				text_exit.appendChild(text);
				var childNodes = log_window.childNodes;
				document.getElementById('incoming_messages').innerHTML = "";
			}
		}
		else 
		{
			alert("Please, fill in login form!");
		}	
	}
}
function exit()
{
	if(online == true)
	{
		document.getElementById('name').value = login;
		login = '';
		online = false;
		var exit_text = document.getElementById('text_exit');
		exit_text.removeChild(exit_text.lastChild);
		var exit_window = document.getElementById('exit');
		exit_window.style.display = 'none';
		var log_window = document.getElementById('login');
		log_window.style.display = 'inline';
		document.getElementById('incoming_messages').innerHTML = "";
	}
}

function edit()
{
	var edit_window = document.getElementById('edit');
	var ok_button = document.getElementById('edit_ok');
	var edit_button = document.getElementById('edit_button');
	var exit_button = document.getElementById('exit_button');
	edit_window.style.display = 'inline';
	edit_ok.style.display = 'inline';
	edit_button.style.display = 'none';
	exit_button.style.display = 'none';
	edit_window.value = login;
}

function ok_edit()
{
	var edit_window = document.getElementById('edit');
	var ok_button = document.getElementById('edit_ok');
	var edit_button = document.getElementById('edit_button');
	var exit_button = document.getElementById('exit_button');
	edit_window.style.display = 'none';
	edit_ok.style.display = 'none';
	edit_button.style.display = 'inline';
	exit_button.style.display = 'inline';
	if(edit_window.value != "")
	{
		if(submit(edit_window.value))
		{
			login = edit_window.value;
			var div_exit = document.getElementById('exit');
			var old_text_exit = document.getElementById('text_exit');
			var text_exit = document.createElement('p')
			text_exit.setAttribute("id", "text_exit");
			var text = document.createTextNode("You  " + login);
			text_exit.appendChild(text);
			div_exit.removeChild(old_text_exit);
			div_exit.insertBefore(text_exit, div_exit.firstChild);
		}
	}
}
function submit(login)
{
	var ok = true;
	var name_template = /[^\.\_\-]$/ig;
	if(!name_template.test(login))
	{
		alert("Invalid name!");
		ok = false;
	}
	return ok;
}

