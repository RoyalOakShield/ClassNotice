package com.classnotice.services;

import com.classnotice.db.entities.Notice;
import com.classnotice.db.entities.NoticeStatus;
import com.classnotice.db.NoticeStatusDAO;
import com.classnotice.db.NoticeDAO;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.ToLongFunction;

@Service
public class NoticeService {

	@Autowired
	private NoticeStatusDAO noticeStatusDao;
	@Autowired
	private NoticeDAO noticeDao;

	public int countUnreadNotice(String uid){
		return noticeStatusDao.queryNoticeCount(uid,false,false,NoticeStatusDAO.READ);
	}

	public int countReadNotice(String uid){
		return noticeStatusDao.queryNoticeCount(uid,false,true,NoticeStatusDAO.READ);
	}

	public int countStarNotice(String uid){
		return noticeStatusDao.queryNoticeCount(uid,true,false,NoticeStatusDAO.STAR);
	}

	public int countTotalNotice(String uid){
		return noticeStatusDao.queryNoticeCount(uid,false,false,0);
	}

	public List<Notice> getUnreadNotice(String uid){
		List<NoticeStatus> statusUnread=noticeStatusDao.query(uid,false,false,NoticeStatusDAO.READ);
		return convertNoticeStatusToNotice(statusUnread);
	}

	//Helper function
	private List<Notice> convertNoticeStatusToNotice(List<NoticeStatus> noticeStatuses){
		Iterator<NoticeStatus> statusIterator=noticeStatuses.iterator();
		List<Notice> notices=new ArrayList<Notice>();
		while(statusIterator.hasNext()){
			NoticeStatus noticeStatus=statusIterator.next();
			Notice notice=noticeDao.query(noticeStatus.getNid());
			notices.add(notice);
		}
		notices.sort(Comparator.comparingLong(new ToLongFunction<Notice>(){
			public long applyAsLong(Notice notice){
				return notice.getPublishTime().getTime();
			}
		}).reversed());

		return notices;
	}
}
